package table.eat.now.waiting.waiting_request.application.usecase;

import static table.eat.now.waiting.waiting_request.application.usecase.utils.WaitingRequestValidator.validateRestaurantAuthority;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import table.eat.now.waiting.waiting_request.application.client.RestaurantClient;
import table.eat.now.waiting.waiting_request.application.client.WaitingClient;
import table.eat.now.waiting.waiting_request.application.client.dto.response.GetDailyWaitingInfo;
import table.eat.now.waiting.waiting_request.application.client.dto.response.GetRestaurantInfo;
import table.eat.now.waiting.waiting_request.application.usecase.dto.query.GetCurrentWaitingRequestsAdminQuery;
import table.eat.now.waiting.waiting_request.application.usecase.dto.query.Query;
import table.eat.now.waiting.waiting_request.application.usecase.dto.response.GetWaitingRequestInfo;
import table.eat.now.waiting.waiting_request.application.usecase.dto.response.PageResult;
import table.eat.now.waiting.waiting_request.domain.criteria.CurrentWaitingRequestCriteria;
import table.eat.now.waiting.waiting_request.domain.entity.WaitingRequest;
import table.eat.now.waiting.waiting_request.domain.info.Paginated;
import table.eat.now.waiting.waiting_request.domain.reader.WaitingRequestReader;
import table.eat.now.waiting.waiting_request.domain.service.WaitingRequestDomainService;

@RequiredArgsConstructor
@Service
public class GetCurrentWaitingRequestsAdminUsecase
    implements QueryUsecase<GetCurrentWaitingRequestsAdminQuery, PageResult<GetWaitingRequestInfo>>{

  private final RestaurantClient restaurantClient;
  private final WaitingClient waitingClient;
  private final WaitingRequestDomainService domainService;
  private final WaitingRequestReader reader;

  @Override
  public Class<? extends Query> getQueryClass() {
    return GetCurrentWaitingRequestsAdminQuery.class;
  }

  @Override
  public PageResult<GetWaitingRequestInfo> execute(GetCurrentWaitingRequestsAdminQuery query) {

    GetDailyWaitingInfo dailyWaitingInfo = waitingClient.getDailyWaitingInfo(query.dailyWaitingUuid());
    GetRestaurantInfo restaurantInfo = restaurantClient.getRestaurantInfo(dailyWaitingInfo.restaurantUuid());
    validateRestaurantAuthority(query.userId(), query.userRole(), restaurantInfo);

    Paginated<WaitingRequest> requests = reader.getCurrentWaitingRequests(
        CurrentWaitingRequestCriteria.from(query));

    PageResult<GetWaitingRequestInfo> requestsInfoPage = PageResult.from(requests)
        .mapWithIndex(
            query.offset(),
            (request, rank) -> {
              return GetWaitingRequestInfo.from(
                  request,
                  dailyWaitingInfo.restaurantName(),
                  rank,
                  domainService.calculateEstimatedWaitingMin(dailyWaitingInfo.avgWaitingSec(), rank));
            });

    return requestsInfoPage;
  }
}
