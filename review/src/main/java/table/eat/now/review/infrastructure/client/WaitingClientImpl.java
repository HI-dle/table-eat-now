package table.eat.now.review.infrastructure.client;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import table.eat.now.review.application.client.WaitingClient;
import table.eat.now.review.application.client.dto.GetServiceInfo;
import table.eat.now.review.infrastructure.client.feign.WaitingFeignClient;

@Component
@RequiredArgsConstructor
public class WaitingClientImpl implements WaitingClient {

  private final WaitingFeignClient waitingFeignClient;

  @Override
  public GetServiceInfo getWaiting(String waitingId) {
    return waitingFeignClient.getWaitingRequest(waitingId).toInfo();
  }
}
