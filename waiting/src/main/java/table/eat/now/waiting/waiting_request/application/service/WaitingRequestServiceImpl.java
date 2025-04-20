package table.eat.now.waiting.waiting_request.application.service;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import table.eat.now.common.exception.CustomException;
import table.eat.now.common.resolver.dto.CurrentUserInfoDto;
import table.eat.now.waiting.waiting_request.application.client.RestaurantClient;
import table.eat.now.waiting.waiting_request.application.client.WaitingClient;
import table.eat.now.waiting.waiting_request.application.dto.request.CreateWaitingRequestCommand;
import table.eat.now.waiting.waiting_request.application.dto.response.GetDailyWaitingInfo;
import table.eat.now.waiting.waiting_request.application.dto.response.GetRestaurantInfo;
import table.eat.now.waiting.waiting_request.application.dto.response.GetWaitingRequestInfo;
import table.eat.now.waiting.waiting_request.application.dto.response.PageResult;
import table.eat.now.waiting.waiting_request.application.event.EventPublisher;
import table.eat.now.waiting.waiting_request.application.event.dto.WaitingRequestCreatedEvent;
import table.eat.now.waiting.waiting_request.application.event.dto.WaitingRequestCreatedInfo;
import table.eat.now.waiting.waiting_request.application.event.dto.WaitingRequestEntranceEvent;
import table.eat.now.waiting.waiting_request.application.event.dto.WaitingRequestEntranceInfo;
import table.eat.now.waiting.waiting_request.application.event.dto.WaitingRequestEvent;
import table.eat.now.waiting.waiting_request.application.event.dto.WaitingRequestPostponedEvent;
import table.eat.now.waiting.waiting_request.application.event.dto.WaitingRequestPostponedInfo;
import table.eat.now.waiting.waiting_request.application.exception.WaitingRequestErrorCode;
import table.eat.now.waiting.waiting_request.application.utils.TimeProvider;
import table.eat.now.waiting.waiting_request.domain.criteria.CurrentWaitingRequestCriteria;
import table.eat.now.waiting.waiting_request.domain.entity.WaitingRequest;
import table.eat.now.waiting.waiting_request.domain.entity.WaitingStatus;
import table.eat.now.waiting.waiting_request.domain.info.Paginated;
import table.eat.now.waiting.waiting_request.domain.repository.WaitingRequestRepository;

@RequiredArgsConstructor
@Service
public class WaitingRequestServiceImpl implements WaitingRequestService {
  private final WaitingRequestRepository waitingRequestRepository;
  private final RestaurantClient restaurantClient;
  private final WaitingClient waitingClient;
  private final EventPublisher<WaitingRequestEvent> eventPublisher;

  @Override
  public String createWaitingRequest(
      CurrentUserInfoDto userInfo, CreateWaitingRequestCommand command) {

    GetDailyWaitingInfo dailyWaitingInfo = waitingClient.getDailyWaitingInfo(
        command.dailyWaitingUuid());

    validateWaitingAvailable(dailyWaitingInfo);
    validateNoDuplicateWaitingRequest(command);

    String waitingRequestUuid = UUID.randomUUID().toString();
    Long sequence = generateSequence(command.dailyWaitingUuid());

    WaitingRequest waitingRequest = command.toEntity(
        waitingRequestUuid, dailyWaitingInfo.restaurantUuid(), userInfo.userId(), sequence);
    waitingRequestRepository.save(waitingRequest);

    Long rank = enqueueWaitingRequestAndGetRank(command.dailyWaitingUuid(), waitingRequestUuid);
    Long estimatedWaitingMin = rank == null ? null : dailyWaitingInfo.avgWaitingSec() * (rank + 1L) /60;

    notifyWaitingRequestCreated(
        command, waitingRequestUuid, dailyWaitingInfo.restaurantName(),
        sequence, rank, estimatedWaitingMin);

    return waitingRequestUuid;
  }

  @Transactional
  @Override
  public void postponeWaitingRequest(CurrentUserInfoDto userInfo, String waitingRequestUuid, String phone) {

    WaitingRequest waitingRequest = getWaitingRequestBy(waitingRequestUuid);
    validateUserPhoneNumber(phone, waitingRequest.getPhone());
    waitingRequest.updateStatus(WaitingStatus.POSTPONED);

    GetDailyWaitingInfo dailyWaitingInfo = waitingClient.getDailyWaitingInfo(
        waitingRequest.getDailyWaitingUuid());

    Long rank = enqueueWaitingRequestAndGetRank(waitingRequest.getDailyWaitingUuid(), waitingRequestUuid);
    Long estimatedWaitingMin = rank == null ? null : dailyWaitingInfo.avgWaitingSec() * (rank + 1L) / 60;
    notifyWaitingRequestPostponed(
        waitingRequest, dailyWaitingInfo.restaurantName(), rank, estimatedWaitingMin);
  }

  @Transactional
  @Override
  public void cancelWaitingRequest(
      CurrentUserInfoDto userInfo, String waitingRequestUuid, String phone) {

    WaitingRequest waitingRequest = getWaitingRequestBy(waitingRequestUuid);
    validateUserPhoneNumber(phone, waitingRequest.getPhone());
    waitingRequest.updateStatus(WaitingStatus.CANCELED);

    dequeueWaitingRequest(waitingRequest.getDailyWaitingUuid(), waitingRequestUuid);
  }

  @Override
  public void processWaitingRequestEntrance(
      CurrentUserInfoDto userInfo, String waitingRequestUuid) {

    WaitingRequest waitingRequest = getWaitingRequestBy(waitingRequestUuid);
    GetRestaurantInfo restaurantInfo = restaurantClient.getRestaurantInfo(waitingRequest.getRestaurantUuid());
    validateRestaurantAuthority(userInfo, restaurantInfo);

    dequeueWaitingRequest(waitingRequest.getDailyWaitingUuid(), waitingRequestUuid);
    notifyWaitingRequestEntrance(waitingRequest, restaurantInfo.name());
  }

  @Override
  public GetWaitingRequestInfo getWaitingRequest(
      CurrentUserInfoDto userInfo, String waitingRequestUuid, String phone) {

    WaitingRequest waitingRequest = getWaitingRequestBy(waitingRequestUuid);
    validateUserPhoneNumber(phone, waitingRequest.getPhone());

    GetDailyWaitingInfo dailyWaitingInfo = waitingClient.getDailyWaitingInfo(
        waitingRequest.getDailyWaitingUuid());
    Long rank = getRankIfWaiting(waitingRequestUuid, waitingRequest);
    Long estimatedWaitingMin = rank == null ? null : dailyWaitingInfo.avgWaitingSec() * (rank + 1L) / 60;

    return GetWaitingRequestInfo.from(waitingRequest, dailyWaitingInfo.restaurantName(), rank, estimatedWaitingMin);
  }

  @Transactional
  @Override
  public void updateWaitingRequestStatusAdmin(
      CurrentUserInfoDto userInfo, String waitingRequestUuid, String type) {

    WaitingRequest waitingRequest = getWaitingRequestBy(waitingRequestUuid);
    GetRestaurantInfo restaurantInfo = restaurantClient.getRestaurantInfo(waitingRequest.getRestaurantUuid());
    validateRestaurantAuthority(userInfo, restaurantInfo);

    waitingRequest.updateStatus(type);
  }

  @Override
  public GetWaitingRequestInfo getWaitingRequestAdmin(CurrentUserInfoDto userInfo, String waitingRequestUuid) {

    WaitingRequest waitingRequest = getWaitingRequestBy(waitingRequestUuid);
    GetRestaurantInfo restaurantInfo = restaurantClient.getRestaurantInfo(waitingRequest.getRestaurantUuid());
    validateRestaurantAuthority(userInfo, restaurantInfo);

    GetDailyWaitingInfo dailyWaitingInfo = waitingClient.getDailyWaitingInfo(
        waitingRequest.getDailyWaitingUuid());

    Long rank = getRankIfWaiting(waitingRequestUuid, waitingRequest);
    Long estimatedWaitingMin = rank == null ? null : dailyWaitingInfo.avgWaitingSec() * (rank + 1L) / 60;

    return GetWaitingRequestInfo.from(waitingRequest, dailyWaitingInfo.restaurantName(), rank, estimatedWaitingMin);
  }

  @Override
  public GetWaitingRequestInfo getWaitingRequestInternal(CurrentUserInfoDto userInfo,
      String waitingRequestUuid) {

    WaitingRequest waitingRequest = getWaitingRequestBy(waitingRequestUuid);
    validateCustomerUserId(userInfo, waitingRequest.getUserId());

    GetDailyWaitingInfo dailyWaitingInfo = waitingClient.getDailyWaitingInfo(
        waitingRequest.getDailyWaitingUuid());

    Long rank = getRankIfWaiting(waitingRequestUuid, waitingRequest);
    Long estimatedWaitingMin = rank == null ? null : dailyWaitingInfo.avgWaitingSec() * (rank + 1L) / 60;

    return GetWaitingRequestInfo.from(waitingRequest, dailyWaitingInfo.restaurantName(), rank, estimatedWaitingMin);
  }

  @Override
  public PageResult<GetWaitingRequestInfo> getCurrentWaitingRequestsAdmin(
      CurrentUserInfoDto userInfo, String dailyWaitingUuid, Pageable pageable) {

    GetDailyWaitingInfo dailyWaitingInfo = waitingClient.getDailyWaitingInfo(dailyWaitingUuid);
    GetRestaurantInfo restaurantInfo = restaurantClient.getRestaurantInfo(dailyWaitingInfo.restaurantUuid());
    validateRestaurantAuthority(userInfo, restaurantInfo);

    Paginated<WaitingRequest> requests = waitingRequestRepository.getCurrentWaitingRequests(
        CurrentWaitingRequestCriteria.from(pageable, dailyWaitingUuid));

    PageResult<GetWaitingRequestInfo> requestsInfoPage = PageResult.from(requests)
        .mapWithIndex(
            pageable.getOffset(),
            (request, rank) -> {
          return GetWaitingRequestInfo.from(request, dailyWaitingInfo.restaurantName(),
              rank, (rank + 1) * dailyWaitingInfo.avgWaitingSec());
        });

    return requestsInfoPage;
  }

  private void notifyWaitingRequestCreated(
      CreateWaitingRequestCommand command, String waitingRequestUuid,
      String restaurantName, Long sequence, Long rank, Long estimatedWaitingMin) {

    WaitingRequestCreatedInfo createdInfo = WaitingRequestCreatedInfo.of(
        waitingRequestUuid, command.phone(), command.slackId(), restaurantName, sequence, rank, estimatedWaitingMin);

    eventPublisher.publish(WaitingRequestCreatedEvent.from(createdInfo));
  }

  private void notifyWaitingRequestPostponed(
      WaitingRequest waitingRequest, String restaurantName, Long rank, Long estimatedWaitingMin) {

    WaitingRequestPostponedInfo postponedInfo = WaitingRequestPostponedInfo.of(
        waitingRequest.getWaitingRequestUuid(), waitingRequest.getPhone(), waitingRequest.getSlackId(),
        restaurantName, waitingRequest.getSequence().longValue(), rank, estimatedWaitingMin);

    eventPublisher.publish(WaitingRequestPostponedEvent.from(postponedInfo));
  }

  private void notifyWaitingRequestEntrance(WaitingRequest waitingRequest, String restaurantName) {

    WaitingRequestEntranceInfo entranceInfo = WaitingRequestEntranceInfo.of(
        waitingRequest.getWaitingRequestUuid(), waitingRequest.getPhone(), waitingRequest.getSlackId(),
        restaurantName, waitingRequest.getSequence().longValue());

    eventPublisher.publish(WaitingRequestEntranceEvent.from(entranceInfo));
  }

  private Long enqueueWaitingRequestAndGetRank(String dailyWaitingUuid, String waitingRequestUuid) {

    long epochMilli = TimeProvider.currentTimeMillis();
    Boolean result = waitingRequestRepository.enqueueWaitingRequest(
        dailyWaitingUuid, waitingRequestUuid, epochMilli);
    if (result == null) {
      throw CustomException.from(WaitingRequestErrorCode.FAILED_ENQUEUE);
    }
    return waitingRequestRepository.getRank(dailyWaitingUuid, waitingRequestUuid);
  }

  private void dequeueWaitingRequest(String dailyWaitingUuid, String waitingRequestUuid) {
    boolean result =
        waitingRequestRepository.dequeueWaitingRequest(dailyWaitingUuid, waitingRequestUuid);
    if (!result) {
      throw CustomException.from(WaitingRequestErrorCode.INVALID_WAITING_REQUEST_UUID);
    }
  }

  private Long generateSequence(String dailyWaitingUuid) {
    return waitingRequestRepository.generateNextSequence(dailyWaitingUuid);
  }

  private Long getRankIfWaiting(String waitingRequestUuid, WaitingRequest waitingRequest) {
    if (!waitingRequest.isWaiting()) {
      return null;
    }
    return waitingRequestRepository.getRank(
        waitingRequest.getDailyWaitingUuid(), waitingRequestUuid);
  }

  private WaitingRequest getWaitingRequestBy(String waitingRequestUuid) {
    return waitingRequestRepository.findByWaitingRequestUuidAndDeletedAtIsNull(waitingRequestUuid)
        .orElseThrow(
            () -> CustomException.from(WaitingRequestErrorCode.INVALID_WAITING_REQUEST_UUID));
  }

  private void validateWaitingAvailable(GetDailyWaitingInfo dailyWaitingInfo) {
    if (!dailyWaitingInfo.isAvailable()) {
      throw CustomException.from(WaitingRequestErrorCode.UNAVAILABLE_WAITING);
    }
  }

  private void validateNoDuplicateWaitingRequest(CreateWaitingRequestCommand command) {
    boolean existsBy = waitingRequestRepository.existsByConditionAndStatusIsWaitingAndDeletedAtIsNull(
        command.dailyWaitingUuid(), command.phone());
    if (existsBy) {
      throw CustomException.from(WaitingRequestErrorCode.ALREADY_EXISTS_WAITING);
    }
  }

  private void validateRestaurantAuthority(CurrentUserInfoDto userInfo, GetRestaurantInfo restaurantInfo) {

    if (userInfo.role().isMaster()) {
      return;
    }
    if (!isOwnerOfRestaurant(userInfo, restaurantInfo.ownerId())
        && !isStaffOfRestaurant(userInfo, restaurantInfo.staffId())) {
      throw CustomException.from(WaitingRequestErrorCode.UNAUTH_REQUEST);
    }
  }

  private static void validateUserPhoneNumber(String phone, String savedPhone) {
    if (!savedPhone.equals(phone)) {
      throw CustomException.from(WaitingRequestErrorCode.UNAUTH_REQUEST);
    }
  }

  private void validateCustomerUserId(CurrentUserInfoDto userInfo, Long userId) {
    if (userInfo.role().isMaster()) {
      return;
    }
    if (!isSameCustomer(userInfo, userId)) {
      throw CustomException.from(WaitingRequestErrorCode.UNAUTH_REQUEST);
    }
  }

  private boolean isSameCustomer(CurrentUserInfoDto userInfo, Long userId) {
    return userInfo.role().isCustomer() && userInfo.userId().equals(userId);
  }

  private boolean isStaffOfRestaurant(CurrentUserInfoDto userInfo, Long staffId) {
    return userInfo.role().isStaff() && staffId.equals(userInfo.userId());
  }

  private boolean isOwnerOfRestaurant(CurrentUserInfoDto userInfo, Long ownerId) {
    return userInfo.role().isOwner() && ownerId.equals(userInfo.userId());
  }
}
