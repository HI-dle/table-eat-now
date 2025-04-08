package table.eat.now.review.infrastructure.client.feign;

import java.util.UUID;
import org.springframework.stereotype.Component;
import table.eat.now.review.application.client.WaitingClient;
import table.eat.now.review.application.service.dto.response.GetServiceInfo;

@Component
public class DummyWaitingClientImpl implements WaitingClient {

	@Override
	public GetServiceInfo getWaiting(UUID waitingId, Long customerId) {
		return new GetServiceInfo(waitingId, customerId);
	}
}
