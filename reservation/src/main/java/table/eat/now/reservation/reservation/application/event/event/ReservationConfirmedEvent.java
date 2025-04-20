/**
 * @author : jieun(je-pa)
 * @Date : 2025. 04. 21.
 */
package table.eat.now.reservation.reservation.application.event.event;

import table.eat.now.reservation.reservation.application.event.payload.ReservationConfirmedPayload;

public record ReservationConfirmedEvent(
    EventType eventType,
    String reservationUuid,
    ReservationConfirmedPayload payload

) implements ReservationEvent {

  public static ReservationConfirmedEvent from(ReservationConfirmedPayload payload){
    return new ReservationConfirmedEvent(EventType.RESERVATION_CONFIRMED, payload.reservationUuid(), payload);
  }

}
