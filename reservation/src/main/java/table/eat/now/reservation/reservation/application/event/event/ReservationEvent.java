package table.eat.now.reservation.reservation.application.event.event;

public interface ReservationEvent {

  EventType eventType();

  String reservationUuid();
}
