package table.eat.now.waiting.waiting_request.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import java.time.LocalDate;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import table.eat.now.waiting.helper.IntegrationTestSupport;
import table.eat.now.waiting.waiting_request.application.client.dto.response.GetDailyWaitingInfo;
import table.eat.now.waiting.waiting_request.application.client.dto.response.GetRestaurantInfo;
import table.eat.now.waiting.waiting_request.application.usecase.dto.query.GetWaitingRequestQuery;
import table.eat.now.waiting.waiting_request.application.usecase.dto.response.GetWaitingRequestInfo;
import table.eat.now.waiting.waiting_request.domain.entity.WaitingRequest;
import table.eat.now.waiting.waiting_request.domain.reader.WaitingRequestReader;
import table.eat.now.waiting.waiting_request.domain.store.WaitingRequestStore;
import table.eat.now.waiting.waiting_request.fixture.WaitingRequestFixture;
import table.eat.now.waiting.waiting_request.infrastructure.store.utils.TimeProvider;

class GetWaitingRequestUsecaseTest extends IntegrationTestSupport {

  @Autowired
  private GetWaitingRequestUsecase usecase;

  @Autowired
  private WaitingRequestReader reader;

  @Autowired
  private WaitingRequestStore store;

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
        .restaurantUuid(waitingRequest.getRestaurantUuid())
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

  @DisplayName("대기 요청 단건 조회 - 성공")
  @Test
  void success() {
    // given
    var query = GetWaitingRequestQuery.of(
        null, null, waitingRequest.getWaitingRequestUuid(), waitingRequest.getPhone());
    // when
    GetWaitingRequestInfo info = usecase.execute(query);

    // then
    assertThat(info.restaurantUuid()).isEqualTo(waitingRequest.getRestaurantUuid());
    assertThat(info.rank()).isEqualTo(0);
    assertThat(info.restaurantName()).isEqualTo(dailyWaitingInfo.restaurantName());
    Long expectedWaitingMin = (info.rank() + 1L) * dailyWaitingInfo.avgWaitingSec() / 60L;
    assertThat(info.estimatedWaitingMin()).isEqualTo(expectedWaitingMin);
  }
}