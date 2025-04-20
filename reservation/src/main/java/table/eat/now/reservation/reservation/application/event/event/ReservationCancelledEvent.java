package table.eat.now.reservation.reservation.application.event.event;

import table.eat.now.reservation.reservation.application.event.payload.ReservationCancelledPayload;

public record ReservationCancelledEvent(
    EventType eventType,
    String reservationUuid,
    ReservationCancelledPayload payload

) implements ReservationEvent {

  public static ReservationCancelledEvent from(ReservationCancelledPayload payload){
    return new ReservationCancelledEvent(EventType.RESERVATION_CANCELLED, payload.reservationUuid(), payload);
  }

}
