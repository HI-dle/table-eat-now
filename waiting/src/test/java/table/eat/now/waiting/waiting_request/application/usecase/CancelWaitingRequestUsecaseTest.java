package table.eat.now.waiting.waiting_request.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;

import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;
import table.eat.now.waiting.helper.IntegrationTestSupport;
import table.eat.now.waiting.waiting_request.application.client.RestaurantClient;
import table.eat.now.waiting.waiting_request.application.client.WaitingClient;
import table.eat.now.waiting.waiting_request.application.messaging.EventPublisher;
import table.eat.now.waiting.waiting_request.application.messaging.dto.WaitingRequestEvent;
import table.eat.now.waiting.waiting_request.application.usecase.dto.command.CancelWaitingRequestCommand;
import table.eat.now.waiting.waiting_request.domain.entity.WaitingRequest;
import table.eat.now.waiting.waiting_request.domain.entity.WaitingStatus;
import table.eat.now.waiting.waiting_request.domain.reader.WaitingRequestReader;
import table.eat.now.waiting.waiting_request.domain.store.WaitingRequestStore;
import table.eat.now.waiting.waiting_request.fixture.WaitingRequestFixture;

class CancelWaitingRequestUsecaseTest extends IntegrationTestSupport {

  @Autowired
  private CancelWaitingRequestUsecase usecase;

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

  @Transactional // 레이지 로딩을 위해 적용
  @DisplayName("대기 요청 취소 처리 검증 - 성공")
  @Test
  void success() {
    // given
    var command = CancelWaitingRequestCommand.of(
        null, null, waitingRequest.getWaitingRequestUuid(), waitingRequest.getPhone());

    // when, then
    assertThatNoException().isThrownBy(() -> usecase.execute(command));

    WaitingRequest modified = reader.getWaitingRequestBy(waitingRequest.getWaitingRequestUuid());
    assertThat(modified.getStatus()).isEqualTo(WaitingStatus.CANCELED);

    var histories = modified.getHistories();
    assertThat(histories.get(0).getStatus()).isEqualTo(WaitingStatus.CANCELED);
  }
}