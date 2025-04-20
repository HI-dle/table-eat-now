package table.eat.now.review.infrastructure.client;

import org.springframework.stereotype.Component;
import table.eat.now.review.application.client.WaitingClient;
import table.eat.now.review.application.client.dto.GetServiceInfo;

@Component
public class DummyWaitingClientImpl implements WaitingClient {

  @Override
  public GetServiceInfo getWaiting(String waitingId) {
    return new GetServiceInfo(waitingId, 1L);
  }
}
