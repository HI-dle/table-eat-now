package table.eat.now.review.application.client;

import table.eat.now.review.application.service.dto.response.GetServiceInfo;

public interface WaitingClient {

  GetServiceInfo getWaiting(String waitingId, Long customerId);
}
