package table.eat.now.review.application.client;

import table.eat.now.review.application.service.dto.response.GetServiceInfo;

public interface ReservationClient {

  GetServiceInfo getReservation(String reservationId, Long customerId);
}
