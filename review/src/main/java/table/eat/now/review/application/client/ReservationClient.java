package table.eat.now.review.application.client;

import table.eat.now.review.application.client.dto.GetServiceInfo;

public interface ReservationClient {

  GetServiceInfo getReservation(String reservationId);
}
