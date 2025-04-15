package table.eat.now.payment.payment.application.client;

import table.eat.now.payment.payment.application.client.dto.GetReservationInfo;

public interface ReservationClient {

  GetReservationInfo getReservation(String reservationUuid);
}
