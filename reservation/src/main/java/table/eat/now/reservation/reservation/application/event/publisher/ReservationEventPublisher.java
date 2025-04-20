/**
 * @author : jieun(je-pa)
 * @Date : 2025. 04. 20.
 */
package table.eat.now.reservation.reservation.application.event.publisher;

import table.eat.now.reservation.reservation.application.event.event.ReservationEvent;

public interface ReservationEventPublisher<T extends ReservationEvent> {

  void publish(T event);
}
