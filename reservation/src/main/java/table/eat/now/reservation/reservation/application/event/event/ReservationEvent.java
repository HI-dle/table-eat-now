/**
 * @author : jieun(je-pa)
 * @Date : 2025. 04. 20.
 */
package table.eat.now.reservation.reservation.application.event.event;

public interface ReservationEvent {

  EventType eventType();

  String reservationUuid();
}
