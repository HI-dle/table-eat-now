package table.eat.now.waiting.waiting_request.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import java.time.LocalDate;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import table.eat.now.common.exception.CustomException;
import table.eat.now.common.resolver.dto.CurrentUserInfoDto;
import table.eat.now.common.resolver.dto.UserRole;
import table.eat.now.waiting.helper.IntegrationTestSupport;
import table.eat.now.waiting.waiting_request.application.client.RestaurantClient;
import table.eat.now.waiting.waiting_request.application.client.WaitingClient;
import table.eat.now.waiting.waiting_request.application.client.dto.response.GetDailyWaitingInfo;
import table.eat.now.waiting.waiting_request.application.client.dto.response.GetRestaurantInfo;
import table.eat.now.waiting.waiting_request.application.exception.WaitingRequestErrorCode;
import table.eat.now.waiting.waiting_request.application.messaging.EventPublisher;
import table.eat.now.waiting.waiting_request.application.messaging.dto.WaitingRequestEvent;
import table.eat.now.waiting.waiting_request.application.usecase.dto.query.GetWaitingRequestInternalQuery;
import table.eat.now.waiting.waiting_request.application.usecase.dto.response.GetWaitingRequestInfo;
import table.eat.now.waiting.waiting_request.domain.entity.WaitingRequest;
import table.eat.now.waiting.waiting_request.domain.reader.WaitingRequestReader;
import table.eat.now.waiting.waiting_request.domain.store.WaitingRequestStore;
import table.eat.now.waiting.waiting_request.fixture.WaitingRequestFixture;
import table.eat.now.waiting.waiting_request.infrastructure.store.utils.TimeProvider;

class GetWaitingRequestInternalUsecaseTest extends IntegrationTestSupport {

  @Autowired
  private GetWaitingRequestInternalUsecase usecase;

  @Autowired
  private WaitingRequestReader reader;

  @Autowired
  private WaitingRequestStore store;

  @MockitoBean
  private RestaurantClient restaurantClient;

  @MockitoBean
  private WaitingClient waitingClient;

  @MockitoBean
  private EventPublisher<WaitingRequestEvent> eventPublisher;

  private WaitingRequest waitingRequest;

  private GetDailyWaitingInfo dailyWaitingInfo;

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

    dailyWaitingInfo = GetDailyWaitingInfo.builder()
        .dailyWaitingUuid(waitingRequest.getDailyWaitingUuid())
        .restaurantUuid(waitingRequest.getRestaurantUuid())
        .waitingDate(LocalDate.now())
        .avgWaitingSec(600L)
        .status("AVAILABLE")
        .build();
    given(waitingClient.getDailyWaitingInfo(waitingRequest.getDailyWaitingUuid())).willReturn(dailyWaitingInfo);
  }

  @DisplayName("내부 대기 요청 단건 조회 - 성공")
  @Test
  void success() {
    // given
    CurrentUserInfoDto userInfo = CurrentUserInfoDto.of(1L, UserRole.CUSTOMER);
    var query = GetWaitingRequestInternalQuery.of(
        userInfo.userId(), userInfo.role(), waitingRequest.getWaitingRequestUuid());

    // when
    GetWaitingRequestInfo info = usecase.execute(query);

    // then
    Long rank = reader.getRank(info.dailyWaitingUuid(), info.waitingRequestUuid());

    assertThat(info.seatSize()).isEqualTo(waitingRequest.getSeatSize());
    assertThat(info.rank()).isEqualTo(rank);
  }

  @DisplayName("내부 대기 요청 단건 조회 - 해당 대기 고객이 아닌 경우 실패")
  @Test
  void failWhenNotAuthorizedCustomer() {
    // given
    CurrentUserInfoDto userInfo = CurrentUserInfoDto.of(2L, UserRole.CUSTOMER);
    var query = GetWaitingRequestInternalQuery.of(
        userInfo.userId(), userInfo.role(), waitingRequest.getWaitingRequestUuid());

    // when, then
    assertThatThrownBy(() -> usecase.execute(query))
        .isInstanceOf(CustomException.class)
        .hasMessage(WaitingRequestErrorCode.UNAUTH_REQUEST.getMessage());
  }
}