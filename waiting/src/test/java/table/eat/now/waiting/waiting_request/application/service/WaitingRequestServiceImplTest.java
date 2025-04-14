package table.eat.now.waiting.waiting_request.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import table.eat.now.common.exception.CustomException;
import table.eat.now.common.resolver.dto.CurrentUserInfoDto;
import table.eat.now.common.resolver.dto.UserRole;
import table.eat.now.waiting.helper.IntegrationTestSupport;
import table.eat.now.waiting.waiting_request.application.client.RestaurantClient;
import table.eat.now.waiting.waiting_request.application.dto.request.CreateWaitingRequestCommand;
import table.eat.now.waiting.waiting_request.application.dto.response.GetRestaurantInfo;
import table.eat.now.waiting.waiting_request.application.exception.WaitingRequestErrorCode;
import table.eat.now.waiting.waiting_request.application.utils.TimeProvider;
import table.eat.now.waiting.waiting_request.domain.entity.WaitingRequest;
import table.eat.now.waiting.waiting_request.domain.repository.WaitingRequestRepository;
import table.eat.now.waiting.waiting_request.fixture.WaitingRequestFixture;

class WaitingRequestServiceImplTest extends IntegrationTestSupport {

  @Autowired
  private WaitingRequestService waitingRequestService;

  @Autowired
  private WaitingRequestRepository waitingRequestRepository;

  @MockitoBean
  private RestaurantClient restaurantClient;

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

    @DisplayName("생성 성공")
    @Test
    void createWaitingRequest() {
      // given
      CurrentUserInfoDto userInfo = CurrentUserInfoDto.of(2L, UserRole.CUSTOMER);
      CreateWaitingRequestCommand command = CreateWaitingRequestCommand.builder()
          .dailyWaitingUuid(UUID.randomUUID().toString())
          .phone("01000000000")
          .slackId("slack@slack.com")
          .seatSize(3)
          .build();

      // when
      String waitingRequestUuid = waitingRequestService.createWaitingRequest(userInfo, command);

      // then
      WaitingRequest waitingRequest =
          waitingRequestRepository.findByWaitingRequestUuidAndDeletedAtIsNull(waitingRequestUuid)
              .orElseThrow(() -> CustomException.from(WaitingRequestErrorCode.INVALID_WAITING_REQUEST_UUID));
      Integer sequence = waitingRequestRepository.getLastWaitingSequence(command.dailyWaitingUuid());

      assertThat(waitingRequest.getDailyWaitingUuid()).isEqualTo(command.dailyWaitingUuid());
      assertThat(waitingRequest.getWaitingRequestUuid()).isEqualTo(waitingRequestUuid);
      assertThat(waitingRequest.getSequence()).isEqualTo(sequence);
    }

    @DisplayName("이미 대기 요청한 휴대전화 번호로 재요청하는 경우 생성 실패")
    @Test
    void failCreateWaitingRequestForDuplicatePhone() {
      // given
      CurrentUserInfoDto userInfo = CurrentUserInfoDto.of(2L, UserRole.CUSTOMER);
      CreateWaitingRequestCommand command = CreateWaitingRequestCommand.builder()
          .dailyWaitingUuid(waitingRequest.getDailyWaitingUuid())
          .phone("01012345678")
          .slackId("slack@slack.com")
          .seatSize(3)
          .build();

      // when, then
      assertThatThrownBy(() -> waitingRequestService.createWaitingRequest(userInfo, command))
          .isInstanceOf(CustomException.class)
          .hasMessage(WaitingRequestErrorCode.ALREADY_EXISTS_WAITING.getMessage());
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

      // when, then
      assertThatNoException().isThrownBy(() -> waitingRequestService.processWaitingRequestEntrance(
          userInfo, waitingRequest.getWaitingRequestUuid()));
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
}