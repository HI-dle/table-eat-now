package table.eat.now.payment.payment.application.client;

import table.eat.now.payment.payment.application.dto.response.GetReservationInfo;

public interface ReservationClient {

  GetReservationInfo getReservation(String reservationUuid);
}
