package table.eat.now.waiting.waiting_request.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import table.eat.now.common.resolver.dto.CurrentUserInfoDto;
import table.eat.now.common.resolver.dto.UserRole;
import table.eat.now.waiting.helper.IntegrationTestSupport;
import table.eat.now.waiting.waiting_request.application.client.dto.response.GetDailyWaitingInfo;
import table.eat.now.waiting.waiting_request.application.client.dto.response.GetRestaurantInfo;
import table.eat.now.waiting.waiting_request.application.usecase.dto.query.GetCurrentWaitingRequestsAdminQuery;
import table.eat.now.waiting.waiting_request.application.usecase.dto.response.GetWaitingRequestInfo;
import table.eat.now.waiting.waiting_request.application.usecase.dto.response.PageResult;
import table.eat.now.waiting.waiting_request.domain.entity.WaitingRequest;
import table.eat.now.waiting.waiting_request.domain.reader.WaitingRequestReader;
import table.eat.now.waiting.waiting_request.domain.store.WaitingRequestStore;
import table.eat.now.waiting.waiting_request.fixture.WaitingRequestFixture;
import table.eat.now.waiting.waiting_request.infrastructure.store.utils.TimeProvider;

class GetCurrentWaitingRequestsAdminUsecaseTest  extends IntegrationTestSupport {

  @Autowired
  private GetCurrentWaitingRequestsAdminUsecase usecase;

  @Autowired
  private WaitingRequestReader reader;

  @Autowired
  private WaitingRequestStore store;

  private WaitingRequest waitingRequest;
  private List<WaitingRequest> waitingRequests;
  private GetDailyWaitingInfo dailyWaitingInfo;

  @BeforeEach
  void setUp() {
    var restaurantUuid = UUID.randomUUID().toString();
    waitingRequest = WaitingRequestFixture.create(UUID.randomUUID().toString(), restaurantUuid,
        "01012345678", 1);
    store.save(waitingRequest);
    store.enqueueWaitingRequest(
        waitingRequest.getDailyWaitingUuid(),
        waitingRequest.getWaitingRequestUuid());

    waitingRequests = WaitingRequestFixture.createList(waitingRequest.getDailyWaitingUuid(),
        waitingRequest.getRestaurantUuid(), 10);

    long l = 0L;
    for (WaitingRequest request : waitingRequests) {
      store.save(request);
      store.enqueueWaitingRequest(
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

  @DisplayName("어드민 현재 대기 요청 목록 조회 - 성공")
  @Test
  void success() {
    // given
    CurrentUserInfoDto userInfo = CurrentUserInfoDto.of(4L, UserRole.STAFF);
    Pageable pageable = PageRequest.of(0, 10);
    var query = GetCurrentWaitingRequestsAdminQuery.of(
        userInfo.userId(), userInfo.role(), waitingRequest.getDailyWaitingUuid(),
        pageable.getPageNumber(), pageable.getPageSize(), pageable.getOffset());

    // when
    PageResult<GetWaitingRequestInfo> pageResult = usecase.execute(query);

    // then
    assertThat(pageResult.totalElements()).isEqualTo(11);
    assertThat(pageResult.contents().size()).isEqualTo(10);
    assertThat(pageResult.totalPages()).isEqualTo(2);
    assertThat(pageResult.contents().get(0).waitingRequestUuid()).isEqualTo(waitingRequest.getWaitingRequestUuid());
    assertThat(pageResult.contents().get(1).waitingRequestUuid()).isEqualTo(waitingRequests.get(0).getWaitingRequestUuid());
    assertThat(pageResult.contents().get(2).waitingRequestUuid()).isEqualTo(waitingRequests.get(1).getWaitingRequestUuid());
  }
}