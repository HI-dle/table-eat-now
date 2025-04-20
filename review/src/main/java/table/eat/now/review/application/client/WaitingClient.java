package table.eat.now.review.application.client;

import table.eat.now.review.application.client.dto.GetServiceInfo;

public interface WaitingClient {

  GetServiceInfo getWaiting(String waitingId);
}
