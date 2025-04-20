package table.eat.now.reservation.reservation.application.event.event;

import table.eat.now.reservation.reservation.application.event.payload.ReservationCanceledPayload;

public record ReservationCanceledEvent (
    EventType eventType,
    String reservationUuid,
    ReservationCanceledPayload payload

) implements ReservationEvent {

  public static ReservationCanceledEvent from(ReservationCanceledPayload payload){
    return new ReservationCanceledEvent(EventType.RESERVATION_CANCELED, payload.reservationUuid(), payload);
  }

}
