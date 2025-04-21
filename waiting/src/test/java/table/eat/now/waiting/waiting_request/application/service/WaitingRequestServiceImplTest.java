package table.eat.now.waiting.waiting_request.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;
import table.eat.now.common.exception.CustomException;
import table.eat.now.common.resolver.dto.CurrentUserInfoDto;
import table.eat.now.common.resolver.dto.UserRole;
import table.eat.now.waiting.helper.IntegrationTestSupport;
import table.eat.now.waiting.waiting_request.application.client.RestaurantClient;
import table.eat.now.waiting.waiting_request.application.client.WaitingClient;
import table.eat.now.waiting.waiting_request.application.dto.request.CreateWaitingRequestCommand;
import table.eat.now.waiting.waiting_request.application.dto.response.GetDailyWaitingInfo;
import table.eat.now.waiting.waiting_request.application.dto.response.GetRestaurantInfo;
import table.eat.now.waiting.waiting_request.application.dto.response.GetWaitingRequestInfo;
import table.eat.now.waiting.waiting_request.application.dto.response.PageResult;
import table.eat.now.waiting.waiting_request.application.event.EventPublisher;
import table.eat.now.waiting.waiting_request.application.event.dto.EventType;
import table.eat.now.waiting.waiting_request.application.event.dto.WaitingRequestCreatedEvent;
import table.eat.now.waiting.waiting_request.application.event.dto.WaitingRequestEntranceEvent;
import table.eat.now.waiting.waiting_request.application.event.dto.WaitingRequestEvent;
import table.eat.now.waiting.waiting_request.application.event.dto.WaitingRequestPostponedEvent;
import table.eat.now.waiting.waiting_request.application.exception.WaitingRequestErrorCode;
import table.eat.now.waiting.waiting_request.application.utils.TimeProvider;
import table.eat.now.waiting.waiting_request.domain.entity.WaitingRequest;
import table.eat.now.waiting.waiting_request.domain.entity.WaitingStatus;
import table.eat.now.waiting.waiting_request.domain.repository.WaitingRequestRepository;
import table.eat.now.waiting.waiting_request.fixture.WaitingRequestFixture;

class WaitingRequestServiceImplTest extends IntegrationTestSupport {

  @Autowired
  private WaitingRequestService waitingRequestService;

  @Autowired
  private WaitingRequestRepository waitingRequestRepository;

  @MockitoBean
  private RestaurantClient restaurantClient;

  @MockitoBean
  private WaitingClient waitingClient;

  @MockitoBean
  private EventPublisher<WaitingRequestEvent> eventPublisher;

  private WaitingRequest waitingRequest;

  @BeforeEach
  void setUp() {
    var restaurantUuid = UUID.randomUUID().toString();
    waitingRequest = WaitingRequestFixture.create(UUID.randomUUID().toString(), restaurantUuid,
        "01012345678", 1);
    waitingRequestRepository.save(waitingRequest);
    waitingRequestRepository.enqueueWaitingRequest(
        waitingRequest.getDailyWaitingUuid(),
        waitingRequest.getWaitingRequestUuid(),
        TimeProvider.currentTimeMillis());
  }

  @DisplayName("대기 요청 생성 검증")
  @Nested
  class createWaitingRequest {

    @BeforeEach
    void setUp() {
    }

    @DisplayName("생성 성공")
    @Test
    void createWaitingRequest() {
      // given
      var userInfo = CurrentUserInfoDto.of(2L, UserRole.CUSTOMER);
      var command = CreateWaitingRequestCommand.builder()
          .dailyWaitingUuid(UUID.randomUUID().toString())
          .phone("01000000000")
          .slackId("slack@slack.com")
          .seatSize(3)
          .build();
      var dailyWaitingInfo = GetDailyWaitingInfo.builder()
          .dailyWaitingUuid(command.dailyWaitingUuid())
          .restaurantUuid(UUID.randomUUID().toString())
          .waitingDate(LocalDate.now())
          .avgWaitingSec(600L)
          .status("AVAILABLE")
          .build();
      given(waitingClient.getDailyWaitingInfo(command.dailyWaitingUuid())).willReturn(dailyWaitingInfo);
      doNothing().when(eventPublisher).publish(any(WaitingRequestCreatedEvent.class));

      // when
      String waitingRequestUuid = waitingRequestService.createWaitingRequest(userInfo, command);

      // then
      WaitingRequest waitingRequest =
          waitingRequestRepository.findByWaitingRequestUuidAndDeletedAtIsNull(waitingRequestUuid)
              .orElseThrow(() -> CustomException.from(WaitingRequestErrorCode.INVALID_WAITING_REQUEST_UUID));
      Integer sequence = waitingRequestRepository.getLastWaitingSequence(command.dailyWaitingUuid());

      assertThat(waitingRequest.getDailyWaitingUuid()).isEqualTo(command.dailyWaitingUuid());
      assertThat(waitingRequest.getWaitingRequestUuid()).isEqualTo(waitingRequestUuid);
      assertThat(waitingRequest.getRestaurantUuid()).isEqualTo(dailyWaitingInfo.restaurantUuid());
      assertThat(waitingRequest.getSequence()).isEqualTo(sequence);

      verify(eventPublisher).publish(any(WaitingRequestCreatedEvent.class));
    }

    @DisplayName("이미 대기 요청한 휴대전화 번호로 재요청하는 경우 생성 실패")
    @Test
    void failCreateWaitingRequestForDuplicatePhone() {
      // given
      var userInfo = CurrentUserInfoDto.of(2L, UserRole.CUSTOMER);
      var command = CreateWaitingRequestCommand.builder()
          .dailyWaitingUuid(waitingRequest.getDailyWaitingUuid())
          .phone("01012345678")
          .slackId("slack@slack.com")
          .seatSize(3)
          .build();

      var dailyWaitingInfo = GetDailyWaitingInfo.builder()
          .dailyWaitingUuid(waitingRequest.getDailyWaitingUuid())
          .restaurantUuid(waitingRequest.getRestaurantUuid())
          .waitingDate(LocalDate.now())
          .avgWaitingSec(600L)
          .status("AVAILABLE")
          .build();
      given(waitingClient.getDailyWaitingInfo(command.dailyWaitingUuid())).willReturn(dailyWaitingInfo);

      // when, then
      assertThatThrownBy(() -> waitingRequestService.createWaitingRequest(userInfo, command))
          .isInstanceOf(CustomException.class)
          .hasMessage(WaitingRequestErrorCode.ALREADY_EXISTS_WAITING.getMessage());
    }

    @DisplayName("식당이 대기 불가능한 경우 생성 실패")
    @Test
    void failCreateWaitingRequestForUnavailableRestaurant() {
      // given
      var userInfo = CurrentUserInfoDto.of(2L, UserRole.CUSTOMER);
      var command = CreateWaitingRequestCommand.builder()
          .dailyWaitingUuid(UUID.randomUUID().toString())
          .phone("01012345678")
          .slackId("slack@slack.com")
          .seatSize(3)
          .build();

      var dailyWaitingInfo = GetDailyWaitingInfo.builder()
          .dailyWaitingUuid(command.dailyWaitingUuid())
          .restaurantUuid(UUID.randomUUID().toString())
          .waitingDate(LocalDate.now())
          .avgWaitingSec(600L)
          .status("UNAVAILABLE")
          .build();
      given(waitingClient.getDailyWaitingInfo(command.dailyWaitingUuid())).willReturn(dailyWaitingInfo);

      // when, then
      assertThatThrownBy(() -> waitingRequestService.createWaitingRequest(userInfo, command))
          .isInstanceOf(CustomException.class)
          .hasMessage(WaitingRequestErrorCode.UNAVAILABLE_WAITING.getMessage());
    }
  }

  @DisplayName("대기 요청 입장 요청 처리 검증")
  @Nested
  class processWaitingRequestEntrance {

    @BeforeEach
    void setUp() {
      GetRestaurantInfo restaurantInfo = GetRestaurantInfo.builder()
          .restaurantUuid(UUID.randomUUID().toString())
          .ownerId(3L)
          .staffId(4L)
          .build();

      given(restaurantClient.getRestaurantInfo(any())).willReturn(restaurantInfo);
    }

    @DisplayName("입장 처리 성공")
    @Test
    void success() {
      // given
      CurrentUserInfoDto userInfo = CurrentUserInfoDto.of(4L, UserRole.STAFF);
      doNothing().when(eventPublisher).publish(any(WaitingRequestEntranceEvent.class));

      // when, then
      assertThatNoException().isThrownBy(() -> waitingRequestService.processWaitingRequestEntrance(
          userInfo, waitingRequest.getWaitingRequestUuid()));
      verify(eventPublisher).publish(any(WaitingRequestEntranceEvent.class));
    }

    @DisplayName("해당 레스토랑의 직원이 아닌 경우 실패")
    @Test
    void failWithUnauthorizedOwner() {
      // given
      CurrentUserInfoDto userInfo = CurrentUserInfoDto.of(6L, UserRole.OWNER);

      // when, then
      assertThatThrownBy(() -> waitingRequestService.processWaitingRequestEntrance(
          userInfo, waitingRequest.getWaitingRequestUuid()))
          .isInstanceOf(CustomException.class)
          .hasMessage(WaitingRequestErrorCode.UNAUTH_REQUEST.getMessage());
    }

    @DisplayName("등록되지 않은 대기 요청인 경우 처리 실패")
    @Test
    void failToProcessWaitingRequestForInvalidUuid() {
      // given
      CurrentUserInfoDto userInfo = CurrentUserInfoDto.of(4L, UserRole.STAFF);

      // when, then
      assertThatThrownBy(() -> waitingRequestService.processWaitingRequestEntrance(
          userInfo, UUID.randomUUID().toString()))
          .isInstanceOf(CustomException.class)
          .hasMessage(WaitingRequestErrorCode.INVALID_WAITING_REQUEST_UUID.getMessage());
    }
  }

  @DisplayName("대기 요청 단건 조회")
  @Nested
  class getWaitingRequest {

    private GetDailyWaitingInfo dailyWaitingInfo;

    @BeforeEach
    void setUp() {
      GetRestaurantInfo restaurantInfo = GetRestaurantInfo.builder()
          .restaurantUuid(UUID.randomUUID().toString())
          .ownerId(3L)
          .staffId(4L)
          .build();
      given(restaurantClient.getRestaurantInfo(any())).willReturn(restaurantInfo);

      dailyWaitingInfo = GetDailyWaitingInfo.builder()
          .dailyWaitingUuid(waitingRequest.getDailyWaitingUuid())
          .restaurantUuid(waitingRequest.getRestaurantUuid())
          .waitingDate(LocalDate.now())
          .avgWaitingSec(600L)
          .status("AVAILABLE")
          .build();
      given(waitingClient.getDailyWaitingInfo(waitingRequest.getDailyWaitingUuid())).willReturn(dailyWaitingInfo);
    }

    @DisplayName("조회 성공")
    @Test
    void success() {
      // given
      // when
      GetWaitingRequestInfo info = waitingRequestService.getWaitingRequest(
          null, waitingRequest.getWaitingRequestUuid(), waitingRequest.getPhone());

      // then
      assertThat(info.restaurantUuid()).isEqualTo(waitingRequest.getRestaurantUuid());
      assertThat(info.rank()).isEqualTo(0);
      assertThat(info.restaurantName()).isEqualTo(dailyWaitingInfo.restaurantName());
      assertThat(info.estimatedWaitingMin()).isEqualTo((info.rank() + 1L) * dailyWaitingInfo.avgWaitingSec() / 60L);
    }

    @DisplayName("admin 조회 성공")
    @Test
    void successAdmin() {
      // given
      CurrentUserInfoDto userInfo = CurrentUserInfoDto.of(4L, UserRole.STAFF);

      // when
      GetWaitingRequestInfo info = waitingRequestService.getWaitingRequestAdmin(
          userInfo, waitingRequest.getWaitingRequestUuid());

      // then
      assertThat(info.restaurantUuid()).isEqualTo(waitingRequest.getRestaurantUuid());
      assertThat(info.rank()).isEqualTo(0);
      assertThat(info.restaurantName()).isEqualTo(dailyWaitingInfo.restaurantName());
      assertThat(info.estimatedWaitingMin()).isEqualTo((info.rank() + 1L) * dailyWaitingInfo.avgWaitingSec() / 60L);
    }
  }

  @DisplayName("대기 연기 요청 검증")
  @Nested
  class postponeWaitingRequest {

    private GetDailyWaitingInfo dailyWaitingInfo;

    @BeforeEach
    void setUp() {
      dailyWaitingInfo = GetDailyWaitingInfo.builder()
          .dailyWaitingUuid(waitingRequest.getDailyWaitingUuid())
          .restaurantUuid(waitingRequest.getRestaurantUuid())
          .waitingDate(LocalDate.now())
          .avgWaitingSec(600L)
          .status("AVAILABLE")
          .build();
      given(waitingClient.getDailyWaitingInfo(waitingRequest.getDailyWaitingUuid())).willReturn(dailyWaitingInfo);
    }

    @Transactional
    @DisplayName("성공")
    @Test
    void success() {
      // given
      doNothing().when(eventPublisher).publish(any(WaitingRequestPostponedEvent.class));
      ArgumentCaptor<WaitingRequestPostponedEvent> captor = ArgumentCaptor.forClass(
          WaitingRequestPostponedEvent.class);

      // when, then
      assertThatNoException().isThrownBy(() -> waitingRequestService.postponeWaitingRequest(
          null, waitingRequest.getWaitingRequestUuid(), waitingRequest.getPhone()));

      verify(eventPublisher).publish(captor.capture());
      WaitingRequestPostponedEvent event = captor.getValue();
      assertThat(event.waitingRequestUuid()).isEqualTo(waitingRequest.getWaitingRequestUuid());
      assertThat(event.eventType()).isEqualTo(EventType.WAITING_POSTPONED);

      WaitingRequest modified = waitingRequestRepository.findByWaitingRequestUuidAndDeletedAtIsNull(
              waitingRequest.getWaitingRequestUuid())
          .orElseThrow(
              () -> CustomException.from(WaitingRequestErrorCode.INVALID_WAITING_REQUEST_UUID));
      assertThat(modified.getStatus()).isEqualTo(WaitingStatus.POSTPONED);

      var histories = modified.getHistories();
      assertThat(histories.get(0).getStatus()).isEqualTo(WaitingStatus.POSTPONED);
    }
  }

  @DisplayName("admin 대기 요청 목록 조회")
  @Nested
  class getCurrentWaitingRequestsAdmin {

    private List<WaitingRequest> waitingRequests;
    private GetDailyWaitingInfo dailyWaitingInfo;

    @BeforeEach
    void setUp() {
      waitingRequests = WaitingRequestFixture.createList(waitingRequest.getDailyWaitingUuid(),
          waitingRequest.getRestaurantUuid(), 10);

      long l = 0L;
      for (WaitingRequest request : waitingRequests) {
        waitingRequestRepository.save(request);
        waitingRequestRepository.enqueueWaitingRequest(
            request.getDailyWaitingUuid(),
            request.getWaitingRequestUuid(),
            TimeProvider.currentTimeMillis() + l);
        l += 1000L;
      }

      dailyWaitingInfo = GetDailyWaitingInfo.builder()
          .dailyWaitingUuid(waitingRequest.getDailyWaitingUuid())
          .restaurantUuid(waitingRequest.getRestaurantUuid())
          .waitingDate(LocalDate.now())
          .avgWaitingSec(600L)
          .status("AVAILABLE")
          .build();
      given(waitingClient.getDailyWaitingInfo(waitingRequest.getDailyWaitingUuid())).willReturn(dailyWaitingInfo);

      GetRestaurantInfo restaurantInfo = GetRestaurantInfo.builder()
          .restaurantUuid(UUID.randomUUID().toString())
          .ownerId(3L)
          .staffId(4L)
          .build();

      given(restaurantClient.getRestaurantInfo(any())).willReturn(restaurantInfo);
    }

    @DisplayName("성공")
    @Test
    void success() {
      // given
      CurrentUserInfoDto userInfo = CurrentUserInfoDto.of(4L, UserRole.STAFF);
      Pageable pageable = PageRequest.of(0, 10);

      // when
      PageResult<GetWaitingRequestInfo> pageResult = waitingRequestService.getCurrentWaitingRequestsAdmin(
          userInfo, waitingRequest.getDailyWaitingUuid(), pageable);

      // then
      assertThat(pageResult.totalElements()).isEqualTo(11);
      assertThat(pageResult.contents().size()).isEqualTo(10);
      assertThat(pageResult.totalPages()).isEqualTo(2);
      assertThat(pageResult.contents().get(0).waitingRequestUuid()).isEqualTo(waitingRequest.getWaitingRequestUuid());
      assertThat(pageResult.contents().get(1).waitingRequestUuid()).isEqualTo(waitingRequests.get(0).getWaitingRequestUuid());
      assertThat(pageResult.contents().get(2).waitingRequestUuid()).isEqualTo(waitingRequests.get(1).getWaitingRequestUuid());
    }
  }

  @DisplayName("대기 요청 취소 처리 검증")
  @Nested
  class cancelWaitingRequest {

    @Transactional // 레이지 로딩을 위해 적용
    @DisplayName("성공")
    @Test
    void success() {
      // given
      // when, then
      assertThatNoException().isThrownBy(() -> waitingRequestService.cancelWaitingRequest(
          null, waitingRequest.getWaitingRequestUuid(), waitingRequest.getPhone()));

      WaitingRequest modified = waitingRequestRepository.findByWaitingRequestUuidAndDeletedAtIsNull(
              waitingRequest.getWaitingRequestUuid())
          .orElseThrow(
              () -> CustomException.from(WaitingRequestErrorCode.INVALID_WAITING_REQUEST_UUID));
      assertThat(modified.getStatus()).isEqualTo(WaitingStatus.CANCELED);

      var histories = modified.getHistories();
      assertThat(histories.get(0).getStatus()).isEqualTo(WaitingStatus.CANCELED);
    }
  }

  @DisplayName("admin 대기 요청 상태 변경 처리 검증")
  @Nested
  class updateWaitingRequestStatusAdmin {

    @BeforeEach
    void setUp() {
      GetRestaurantInfo restaurantInfo = GetRestaurantInfo.builder()
          .restaurantUuid(UUID.randomUUID().toString())
          .ownerId(3L)
          .staffId(4L)
          .build();

      given(restaurantClient.getRestaurantInfo(any())).willReturn(restaurantInfo);
    }

    @Transactional
    @DisplayName("성공")
    @Test
    void success() {
      // given
      CurrentUserInfoDto userInfo = CurrentUserInfoDto.of(4L, UserRole.STAFF);
      var type = "SEATED";

      // when, then
      assertThatNoException().isThrownBy(() ->
          waitingRequestService.updateWaitingRequestStatusAdmin(
              userInfo, waitingRequest.getWaitingRequestUuid(), type));

      WaitingRequest modified = waitingRequestRepository.findByWaitingRequestUuidAndDeletedAtIsNull(
              waitingRequest.getWaitingRequestUuid())
          .orElseThrow(
              () -> CustomException.from(WaitingRequestErrorCode.INVALID_WAITING_REQUEST_UUID));

      assertThat(modified.getStatus()).isEqualTo(WaitingStatus.valueOf(type));

      var histories = modified.getHistories();
      assertThat(histories.get(0).getStatus()).isEqualTo(WaitingStatus.valueOf(type));
    }
  }

  @DisplayName("내부 대기 요청 조회 검증")
  @Nested
  class getWaitingRequestInternal {

    private GetDailyWaitingInfo dailyWaitingInfo;

    @BeforeEach
    void setUp() {
      dailyWaitingInfo = GetDailyWaitingInfo.builder()
          .dailyWaitingUuid(waitingRequest.getDailyWaitingUuid())
          .restaurantUuid(waitingRequest.getRestaurantUuid())
          .waitingDate(LocalDate.now())
          .avgWaitingSec(600L)
          .status("AVAILABLE")
          .build();
      given(waitingClient.getDailyWaitingInfo(waitingRequest.getDailyWaitingUuid())).willReturn(dailyWaitingInfo);
    }

    @DisplayName("성공")
    @Test
    void success() {
      // given
      CurrentUserInfoDto userInfo = CurrentUserInfoDto.of(1L, UserRole.CUSTOMER);

      // when
      GetWaitingRequestInfo info = waitingRequestService.getWaitingRequestInternal(
          userInfo, waitingRequest.getWaitingRequestUuid());

      // then
      Long rank = waitingRequestRepository.getRank(info.dailyWaitingUuid(), info.waitingRequestUuid());

      assertThat(info.seatSize()).isEqualTo(waitingRequest.getSeatSize());
      assertThat(info.rank()).isEqualTo(rank);
    }

    @DisplayName("해당 대기 고객이 아닌 경우 실패")
    @Test
    void failWhenNotAuthorizedCustomer() {
      // given
      CurrentUserInfoDto userInfo = CurrentUserInfoDto.of(2L, UserRole.CUSTOMER);

      // when, then
      assertThatThrownBy(() -> waitingRequestService.getWaitingRequestInternal(
          userInfo, waitingRequest.getWaitingRequestUuid()))
          .isInstanceOf(CustomException.class)
          .hasMessage(WaitingRequestErrorCode.UNAUTH_REQUEST.getMessage());
    }
  }
}