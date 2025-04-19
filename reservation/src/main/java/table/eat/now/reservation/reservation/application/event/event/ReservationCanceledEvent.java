package table.eat.now.reservation.reservation.application.event.event;

import table.eat.now.reservation.reservation.application.event.payload.ReservationCanceledPayload;

public record ReservationCanceledEvent (
    String reservationUuid,
    ReservationCanceledPayload payload

) implements ReservationEvent {

  public static ReservationCanceledEvent from(ReservationCanceledPayload payload){
    return new ReservationCanceledEvent(payload.reservationUuid(), payload);
  }

  @Override
  public EventType eventType() {
    return EventType.RESERVATION_CANCELED;
  }

}
