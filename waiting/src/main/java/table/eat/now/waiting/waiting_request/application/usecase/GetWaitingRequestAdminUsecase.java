package table.eat.now.waiting.waiting_request.application.usecase;

import static table.eat.now.waiting.waiting_request.application.usecase.utils.WaitingRequestValidator.validateRestaurantAuthority;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import table.eat.now.waiting.waiting_request.application.client.RestaurantClient;
import table.eat.now.waiting.waiting_request.application.client.WaitingClient;
import table.eat.now.waiting.waiting_request.application.client.dto.response.GetDailyWaitingInfo;
import table.eat.now.waiting.waiting_request.application.client.dto.response.GetRestaurantInfo;
import table.eat.now.waiting.waiting_request.application.usecase.dto.query.GetWaitingRequestAdminQuery;
import table.eat.now.waiting.waiting_request.application.usecase.dto.query.Query;
import table.eat.now.waiting.waiting_request.application.usecase.dto.response.GetWaitingRequestInfo;
import table.eat.now.waiting.waiting_request.domain.entity.WaitingRequest;
import table.eat.now.waiting.waiting_request.domain.reader.WaitingRequestReader;
import table.eat.now.waiting.waiting_request.domain.service.WaitingRequestDomainService;

@RequiredArgsConstructor
@Service
public class GetWaitingRequestAdminUsecase implements QueryUsecase<GetWaitingRequestAdminQuery, GetWaitingRequestInfo> {

  private final RestaurantClient restaurantClient;
  private final WaitingClient waitingClient;
  private final WaitingRequestDomainService domainService;
  private final WaitingRequestReader reader;

  @Override
  public Class<? extends Query> getQueryClass() {
    return GetWaitingRequestAdminQuery.class;
  }

  @Override
  public GetWaitingRequestInfo execute(GetWaitingRequestAdminQuery query) {

    WaitingRequest waitingRequest = reader.getWaitingRequestBy(query.waitingRequestUuid());
    GetRestaurantInfo restaurantInfo = restaurantClient.getRestaurantInfo(waitingRequest.getRestaurantUuid());
    validateRestaurantAuthority(query.userId(), query.userRole(), restaurantInfo);

    GetDailyWaitingInfo dailyWaitingInfo = waitingClient.getDailyWaitingInfo(
        waitingRequest.getDailyWaitingUuid());

    Long rank = reader.getRankIfWaiting(query.waitingRequestUuid(), waitingRequest);
    Long estimatedWaitingMin = domainService.calculateEstimatedWaitingMin(dailyWaitingInfo.avgWaitingSec(), rank);

    return GetWaitingRequestInfo.from(waitingRequest, dailyWaitingInfo.restaurantName(), rank, estimatedWaitingMin);
  }
}
