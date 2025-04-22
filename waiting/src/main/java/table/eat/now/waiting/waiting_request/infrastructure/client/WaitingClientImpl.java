package table.eat.now.waiting.waiting_request.infrastructure.client;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import table.eat.now.waiting.waiting_request.application.client.WaitingClient;
import table.eat.now.waiting.waiting_request.application.client.dto.response.GetDailyWaitingInfo;
import table.eat.now.waiting.waiting_request.infrastructure.client.feign.WaitingFeignClient;

@RequiredArgsConstructor
@Component
public class WaitingClientImpl implements WaitingClient {
  private final WaitingFeignClient feignClient;

  @Override
  public GetDailyWaitingInfo getDailyWaitingInfo(String dailyWaitingUuid) {
    return feignClient.getDailyWaitingInfo(dailyWaitingUuid).getBody().toInfo();
  }
}
