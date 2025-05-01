package table.eat.now.waiting.waiting_request.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;

import java.time.LocalDate;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import table.eat.now.common.exception.CustomException;
import table.eat.now.common.resolver.dto.CurrentUserInfoDto;
import table.eat.now.common.resolver.dto.UserRole;
import table.eat.now.waiting.helper.IntegrationTestSupport;
import table.eat.now.waiting.waiting_request.application.client.dto.response.GetDailyWaitingInfo;
import table.eat.now.waiting.waiting_request.application.exception.WaitingRequestErrorCode;
import table.eat.now.waiting.waiting_request.application.messaging.dto.WaitingRequestCreatedEvent;
import table.eat.now.waiting.waiting_request.application.usecase.dto.command.CreateWaitingRequestCommand;
import table.eat.now.waiting.waiting_request.domain.entity.WaitingRequest;
import table.eat.now.waiting.waiting_request.domain.reader.WaitingRequestReader;
import table.eat.now.waiting.waiting_request.domain.store.WaitingRequestStore;
import table.eat.now.waiting.waiting_request.fixture.WaitingRequestFixture;

class CreateWaitingRequestUsecaseTest extends IntegrationTestSupport {

  @Autowired
  private CreateWaitingRequestUsecase usecase;

  @Autowired
  private WaitingRequestReader reader;

  @Autowired
  private WaitingRequestStore store;

  private WaitingRequest waitingRequest;

  @BeforeEach
  void setUp() {
    var restaurantUuid = UUID.randomUUID().toString();
    waitingRequest = WaitingRequestFixture.create(UUID.randomUUID().toString(), restaurantUuid,
        "01012345678", 1);
    store.save(waitingRequest);
    store.enqueueWaitingRequest(
        waitingRequest.getDailyWaitingUuid(),
        waitingRequest.getWaitingRequestUuid());
  }

  @DisplayName("대기 요청 생성 검증 - 생성 성공")
  @Test
  void createWaitingRequest() {
    // given
    var userInfo = CurrentUserInfoDto.of(2L, UserRole.CUSTOMER);
    var command = CreateWaitingRequestCommand.builder()
        .userId(userInfo.userId())
        .userRole(userInfo.role())
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
    String waitingRequestUuid = usecase.execute(command);

    // then
    WaitingRequest waitingRequest =
        reader.getWaitingRequestBy(waitingRequestUuid);
    Integer sequence = reader.getLastWaitingSequence(command.dailyWaitingUuid());

    assertThat(waitingRequest.getDailyWaitingUuid()).isEqualTo(command.dailyWaitingUuid());
    assertThat(waitingRequest.getWaitingRequestUuid()).isEqualTo(waitingRequestUuid);
    assertThat(waitingRequest.getRestaurantUuid()).isEqualTo(dailyWaitingInfo.restaurantUuid());
    assertThat(waitingRequest.getSequence()).isEqualTo(sequence);

    verify(eventPublisher).publish(any(WaitingRequestCreatedEvent.class));
  }

  @DisplayName("대기 요청 생성 검증 - 이미 대기 요청한 휴대전화 번호로 재요청하는 경우 생성 실패")
  @Test
  void failCreateWaitingRequestForDuplicatePhone() {
    // given
    var userInfo = CurrentUserInfoDto.of(2L, UserRole.CUSTOMER);
    var command = CreateWaitingRequestCommand.builder()
        .userId(userInfo.userId())
        .userRole(userInfo.role())
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
    assertThatThrownBy(() -> usecase.execute(command))
        .isInstanceOf(CustomException.class)
        .hasMessage(WaitingRequestErrorCode.ALREADY_EXISTS_WAITING.getMessage());
  }

  @DisplayName("대기 요청 생성 검증 - 식당이 대기 불가능한 경우 생성 실패")
  @Test
  void failCreateWaitingRequestForUnavailableRestaurant() {
    // given
    var userInfo = CurrentUserInfoDto.of(2L, UserRole.CUSTOMER);
    var command = CreateWaitingRequestCommand.builder()
        .userId(userInfo.userId())
        .userRole(userInfo.role())
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
    assertThatThrownBy(() -> usecase.execute(command))
        .isInstanceOf(CustomException.class)
        .hasMessage(WaitingRequestErrorCode.UNAVAILABLE_WAITING.getMessage());
  }
}