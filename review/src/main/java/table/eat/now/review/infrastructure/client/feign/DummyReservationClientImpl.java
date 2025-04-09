package table.eat.now.review.infrastructure.client.feign;

import java.util.UUID;
import org.springframework.stereotype.Component;
import table.eat.now.review.application.client.ReservationClient;
import table.eat.now.review.application.service.dto.response.GetServiceInfo;

@Component
public class DummyReservationClientImpl implements ReservationClient {

	@Override
	public GetServiceInfo getReservation(String reservationId, Long customerId) {
		return new GetServiceInfo(reservationId, customerId);
	}
}
