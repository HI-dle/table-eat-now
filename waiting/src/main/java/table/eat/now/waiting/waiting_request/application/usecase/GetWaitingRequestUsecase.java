package table.eat.now.waiting.waiting_request.application.usecase;

import static table.eat.now.waiting.waiting_request.application.usecase.utils.WaitingRequestValidator.validateUserPhoneNumber;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import table.eat.now.waiting.waiting_request.application.client.WaitingClient;
import table.eat.now.waiting.waiting_request.application.client.dto.response.GetDailyWaitingInfo;
import table.eat.now.waiting.waiting_request.application.usecase.dto.query.GetWaitingRequestQuery;
import table.eat.now.waiting.waiting_request.application.usecase.dto.query.Query;
import table.eat.now.waiting.waiting_request.application.usecase.dto.response.GetWaitingRequestInfo;
import table.eat.now.waiting.waiting_request.domain.entity.WaitingRequest;
import table.eat.now.waiting.waiting_request.domain.reader.WaitingRequestReader;
import table.eat.now.waiting.waiting_request.domain.service.WaitingRequestDomainService;

@RequiredArgsConstructor
@Service
public class GetWaitingRequestUsecase implements QueryUsecase<GetWaitingRequestQuery, GetWaitingRequestInfo> {

  private final WaitingClient waitingClient;
  private final WaitingRequestDomainService domainService;
  private final WaitingRequestReader reader;

  @Override
  public Class<? extends Query> getQueryClass() {
    return GetWaitingRequestQuery.class;
  }

  @Override
  public GetWaitingRequestInfo execute(GetWaitingRequestQuery query) {

    WaitingRequest waitingRequest = reader.getWaitingRequestBy(query.waitingRequestUuid());

    validateUserPhoneNumber(query.phone(), waitingRequest.getPhone());

    GetDailyWaitingInfo dailyWaitingInfo = waitingClient.getDailyWaitingInfo(
        waitingRequest.getDailyWaitingUuid());
    Long rank = reader.getRankIfWaiting(query.waitingRequestUuid(), waitingRequest);
    Long estimatedWaitingMin = domainService.calculateEstimatedWaitingMin(dailyWaitingInfo.avgWaitingSec(), rank);

    return GetWaitingRequestInfo.from(waitingRequest, dailyWaitingInfo.restaurantName(), rank, estimatedWaitingMin);
  }
}
