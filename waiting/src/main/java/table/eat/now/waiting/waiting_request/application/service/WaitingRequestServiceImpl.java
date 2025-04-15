package table.eat.now.waiting.waiting_request.application.service;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import table.eat.now.common.exception.CustomException;
import table.eat.now.common.resolver.dto.CurrentUserInfoDto;
import table.eat.now.waiting.waiting_request.application.client.RestaurantClient;
import table.eat.now.waiting.waiting_request.application.client.WaitingClient;
import table.eat.now.waiting.waiting_request.application.dto.event.WaitingRequestCreatedEvent;
import table.eat.now.waiting.waiting_request.application.dto.request.CreateWaitingRequestCommand;
import table.eat.now.waiting.waiting_request.application.dto.response.GetDailyWaitingInfo;
import table.eat.now.waiting.waiting_request.application.dto.response.GetRestaurantInfo;
import table.eat.now.waiting.waiting_request.application.dto.response.GetWaitingRequestInfo;
import table.eat.now.waiting.waiting_request.application.exception.WaitingRequestErrorCode;
import table.eat.now.waiting.waiting_request.application.utils.TimeProvider;
import table.eat.now.waiting.waiting_request.domain.entity.WaitingRequest;
import table.eat.now.waiting.waiting_request.domain.repository.WaitingRequestRepository;

@RequiredArgsConstructor
@Service
public class WaitingRequestServiceImpl implements WaitingRequestService {
  private final WaitingRequestRepository waitingRequestRepository;
  private final RestaurantClient restaurantClient;
  private final WaitingClient waitingClient;

  @Override
  public String createWaitingRequest(
      CurrentUserInfoDto userInfo, CreateWaitingRequestCommand command) {

    GetDailyWaitingInfo dailyWaitingInfo = waitingClient.getDailyWaitingInfo(
        command.dailyWaitingUuid());

    validateWaitingAvailable(dailyWaitingInfo);
    validateNoDuplicateWaitingRequest(command);

    String waitingRequestUuid = UUID.randomUUID().toString();
    Long sequence = generateSequence(command.dailyWaitingUuid());
    Long rank = enqueueWaitingRequestAndGetRank(command.dailyWaitingUuid(), waitingRequestUuid);
    long estimatedWaitingSec = dailyWaitingInfo.avgWaitingSec() * (rank + 1L);

    WaitingRequest waitingRequest = command.toEntity(
        waitingRequestUuid, dailyWaitingInfo.restaurantUuid(), userInfo.userId(), sequence);
    waitingRequestRepository.save(waitingRequest);

    // todo. 대기 결과 안내 문자 발송
    sendWaitingRequestCreatedMessage(command, dailyWaitingInfo.restaurantName(), sequence, rank, estimatedWaitingSec);

    return waitingRequestUuid;
  }

  private void validateWaitingAvailable(GetDailyWaitingInfo dailyWaitingInfo) {
    if (!dailyWaitingInfo.isAvailable()) {
      throw CustomException.from(WaitingRequestErrorCode.UNAVAILABLE_WAITING);
    }
  }

  @Override
  public void processWaitingRequestEntrance(
      CurrentUserInfoDto userInfo, String waitingRequestsUuid) {

    WaitingRequest waitingRequest = getWaitingRequestBy(waitingRequestsUuid);
    GetRestaurantInfo restaurantInfo = restaurantClient.getRestaurantInfo(waitingRequest.getRestaurantUuid());
    validateRestaurantAuthority(userInfo, restaurantInfo);

    dequeueWaitingRequest(waitingRequest.getDailyWaitingUuid(), waitingRequestsUuid);

    // todo 메세지 전송 필요
    sendWaitingRequestEntranceMessage(waitingRequest, restaurantInfo.name());
  }

  @Override
  public GetWaitingRequestInfo getWaitingRequest(
      CurrentUserInfoDto userInfo, String waitingRequestUuid, String phone) {

    WaitingRequest waitingRequest = getWaitingRequestBy(waitingRequestUuid);
    validateUserPhoneNumber(phone, waitingRequest.getPhone());

    GetDailyWaitingInfo dailyWaitingInfo = waitingClient.getDailyWaitingInfo(
        waitingRequest.getDailyWaitingUuid());

    Long rank = waitingRequestRepository.getRank(waitingRequest.getDailyWaitingUuid(), waitingRequestUuid);
    long estimatedWaitingSec = dailyWaitingInfo.avgWaitingSec() * (rank + 1L);

    return GetWaitingRequestInfo.from(waitingRequest, dailyWaitingInfo.restaurantName(), rank, estimatedWaitingSec);
  }

  @Override
  public GetWaitingRequestInfo getWaitingRequestAdmin(CurrentUserInfoDto userInfo, String waitingRequestUuid) {

    WaitingRequest waitingRequest = getWaitingRequestBy(waitingRequestUuid);
    GetRestaurantInfo restaurantInfo = restaurantClient.getRestaurantInfo(waitingRequest.getRestaurantUuid());
    validateRestaurantAuthority(userInfo, restaurantInfo);

    GetDailyWaitingInfo dailyWaitingInfo = waitingClient.getDailyWaitingInfo(
        waitingRequest.getDailyWaitingUuid());
    Long rank = waitingRequestRepository.getRank(waitingRequest.getDailyWaitingUuid(), waitingRequestUuid);
    long estimatedWaitingSec = dailyWaitingInfo.avgWaitingSec() * (rank + 1L);

    return GetWaitingRequestInfo.from(waitingRequest, dailyWaitingInfo.restaurantName(), rank, estimatedWaitingSec);
  }

  private void sendWaitingRequestCreatedMessage(
      CreateWaitingRequestCommand command, String restaurantName, Long sequence, Long rank, long estimatedWaitingSec) {

    WaitingRequestCreatedEvent event = WaitingRequestCreatedEvent.of(
        command.phone(), command.slackId(), restaurantName, sequence, rank, estimatedWaitingSec);
  }

  private void sendWaitingRequestEntranceMessage(WaitingRequest waitingRequest, String restaurantName) {
  }

  private void dequeueWaitingRequest(String dailyWaitingUuid, String waitingRequestsUuid) {
    boolean result =
        waitingRequestRepository.dequeueWaitingRequest(dailyWaitingUuid, waitingRequestsUuid);
    if (!result) {
      throw CustomException.from(WaitingRequestErrorCode.INVALID_WAITING_REQUEST_UUID);
    }
  }

  private Long enqueueWaitingRequestAndGetRank(String dailyWaitingUuid, String waitingRequestUuid) {

    long epochMilli = TimeProvider.currentTimeMillis();
    boolean result = waitingRequestRepository.enqueueWaitingRequest(
        dailyWaitingUuid, waitingRequestUuid, epochMilli);
    if (!result) {
      throw CustomException.from(WaitingRequestErrorCode.FAILED_ENQUEUE);
    }
    return waitingRequestRepository.getRank(dailyWaitingUuid, waitingRequestUuid);
  }

  private Long generateSequence(String dailyWaitingUuid) {
    return waitingRequestRepository.generateNextSequence(dailyWaitingUuid);
  }

  private WaitingRequest getWaitingRequestBy(String waitingRequestsUuid) {
    return waitingRequestRepository.findByWaitingRequestUuidAndDeletedAtIsNull(waitingRequestsUuid)
        .orElseThrow(
            () -> CustomException.from(WaitingRequestErrorCode.INVALID_WAITING_REQUEST_UUID));
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

  private boolean isStaffOfRestaurant(CurrentUserInfoDto userInfo, Long staffId) {
    return userInfo.role().isStaff() && staffId.equals(userInfo.userId());
  }

  private boolean isOwnerOfRestaurant(CurrentUserInfoDto userInfo, Long ownerId) {
    return userInfo.role().isOwner() && ownerId.equals(userInfo.userId());
  }

  private static void validateUserPhoneNumber(String phone, String savedPhone) {
    if (!savedPhone.equals(phone)) {
      throw CustomException.from(WaitingRequestErrorCode.UNAUTH_REQUEST);
    }
  }
}
