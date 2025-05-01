package table.eat.now.waiting.waiting_request.application.usecase;

import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;

import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import table.eat.now.common.exception.CustomException;
import table.eat.now.common.resolver.dto.CurrentUserInfoDto;
import table.eat.now.common.resolver.dto.UserRole;
import table.eat.now.waiting.helper.IntegrationTestSupport;
import table.eat.now.waiting.waiting_request.application.client.dto.response.GetRestaurantInfo;
import table.eat.now.waiting.waiting_request.application.exception.WaitingRequestErrorCode;
import table.eat.now.waiting.waiting_request.application.messaging.dto.WaitingRequestEntranceEvent;
import table.eat.now.waiting.waiting_request.application.usecase.dto.command.WaitingRequestEntranceAdminCommand;
import table.eat.now.waiting.waiting_request.domain.entity.WaitingRequest;
import table.eat.now.waiting.waiting_request.domain.reader.WaitingRequestReader;
import table.eat.now.waiting.waiting_request.domain.store.WaitingRequestStore;
import table.eat.now.waiting.waiting_request.fixture.WaitingRequestFixture;
import table.eat.now.waiting.waiting_request.infrastructure.store.utils.TimeProvider;

class WaitingRequestEntranceAdminUsecaseTest extends IntegrationTestSupport {

  @Autowired
  private WaitingRequestEntranceAdminUsecase usecase;

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
        waitingRequest.getWaitingRequestUuid(),
        TimeProvider.currentTimeMillis());

    GetRestaurantInfo restaurantInfo = GetRestaurantInfo.builder()
        .restaurantUuid(UUID.randomUUID().toString())
        .ownerId(3L)
        .staffId(4L)
        .build();

    given(restaurantClient.getRestaurantInfo(any())).willReturn(restaurantInfo);
  }

  @DisplayName("어드민 대기 요청 입장 요청 처리 검증 - 성공")
  @Test
  void success() {
    // given
    CurrentUserInfoDto userInfo = CurrentUserInfoDto.of(4L, UserRole.STAFF);
    doNothing().when(eventPublisher).publish(any(WaitingRequestEntranceEvent.class));
    var command = WaitingRequestEntranceAdminCommand.of(
        userInfo.userId(), userInfo.role(), waitingRequest.getWaitingRequestUuid());

    // when, then
    assertThatNoException().isThrownBy(() -> usecase.execute(command));
    verify(eventPublisher).publish(any(WaitingRequestEntranceEvent.class));
  }

  @DisplayName("어드민 대기 요청 입장 요청 처리 검증 - 해당 레스토랑의 직원이 아닌 경우 실패")
  @Test
  void failWithUnauthorizedOwner() {
    // given
    CurrentUserInfoDto userInfo = CurrentUserInfoDto.of(6L, UserRole.OWNER);
    var command = WaitingRequestEntranceAdminCommand.of(
        userInfo.userId(), userInfo.role(), waitingRequest.getWaitingRequestUuid());

    // when, then
    assertThatThrownBy(() -> usecase.execute(command))
        .isInstanceOf(CustomException.class)
        .hasMessage(WaitingRequestErrorCode.UNAUTH_REQUEST.getMessage());
  }

  @DisplayName("어드민 대기 요청 입장 요청 처리 검증 - 등록되지 않은 대기 요청인 경우 처리 실패")
  @Test
  void failToProcessWaitingRequestForInvalidUuid() {
    // given
    CurrentUserInfoDto userInfo = CurrentUserInfoDto.of(4L, UserRole.STAFF);
    var command = WaitingRequestEntranceAdminCommand.of(
        userInfo.userId(), userInfo.role(), UUID.randomUUID().toString());

    // when, then
    assertThatThrownBy(() -> usecase.execute(command))
        .isInstanceOf(CustomException.class)
        .hasMessage(WaitingRequestErrorCode.INVALID_WAITING_REQUEST_UUID.getMessage());
  }
}