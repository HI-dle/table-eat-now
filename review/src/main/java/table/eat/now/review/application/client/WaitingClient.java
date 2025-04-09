package table.eat.now.review.application.client;

import java.util.UUID;
import table.eat.now.review.application.service.dto.response.GetServiceInfo;

public interface WaitingClient {

	GetServiceInfo getWaiting(UUID waitingId, Long customerId);
}
