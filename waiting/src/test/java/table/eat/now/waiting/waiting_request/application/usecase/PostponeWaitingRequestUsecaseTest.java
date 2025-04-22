package table.eat.now.waiting.waiting_request.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;

import java.time.LocalDate;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;
import table.eat.now.waiting.helper.IntegrationTestSupport;
import table.eat.now.waiting.waiting_request.application.client.RestaurantClient;
import table.eat.now.waiting.waiting_request.application.client.WaitingClient;
import table.eat.now.waiting.waiting_request.application.client.dto.response.GetDailyWaitingInfo;
import table.eat.now.waiting.waiting_request.application.client.dto.response.GetRestaurantInfo;
import table.eat.now.waiting.waiting_request.application.messaging.EventPublisher;
import table.eat.now.waiting.waiting_request.application.messaging.dto.EventType;
import table.eat.now.waiting.waiting_request.application.messaging.dto.WaitingRequestEvent;
import table.eat.now.waiting.waiting_request.application.messaging.dto.WaitingRequestPostponedEvent;
import table.eat.now.waiting.waiting_request.application.usecase.dto.command.PostponeWaitingRequestCommand;
import table.eat.now.waiting.waiting_request.domain.entity.WaitingRequest;
import table.eat.now.waiting.waiting_request.domain.entity.WaitingStatus;
import table.eat.now.waiting.waiting_request.domain.reader.WaitingRequestReader;
import table.eat.now.waiting.waiting_request.domain.store.WaitingRequestStore;
import table.eat.now.waiting.waiting_request.fixture.WaitingRequestFixture;
import table.eat.now.waiting.waiting_request.infrastructure.store.utils.TimeProvider;

class PostponeWaitingRequestUsecaseTest extends IntegrationTestSupport {

  @Autowired
  private PostponeWaitingRequestUsecase usecase;

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

  @Transactional
  @DisplayName("대기 연기 요청 검증 - 성공")
  @Test
  void success() {
    // given
    var command = PostponeWaitingRequestCommand.of(
        null, null, waitingRequest.getWaitingRequestUuid(), waitingRequest.getPhone());
    doNothing().when(eventPublisher).publish(any(WaitingRequestPostponedEvent.class));
    ArgumentCaptor<WaitingRequestPostponedEvent> captor = ArgumentCaptor.forClass(
        WaitingRequestPostponedEvent.class);

    // when, then
    assertThatNoException().isThrownBy(() -> usecase.execute(command));

    verify(eventPublisher).publish(captor.capture());
    WaitingRequestPostponedEvent event = captor.getValue();
    assertThat(event.waitingRequestUuid()).isEqualTo(waitingRequest.getWaitingRequestUuid());
    assertThat(event.eventType()).isEqualTo(EventType.WAITING_POSTPONED);

    WaitingRequest modified = reader.getWaitingRequestBy(waitingRequest.getWaitingRequestUuid());
    assertThat(modified.getStatus()).isEqualTo(WaitingStatus.POSTPONED);

    var histories = modified.getHistories();
    assertThat(histories.get(0).getStatus()).isEqualTo(WaitingStatus.POSTPONED);
  }
}