/**
 * @author : jieun(je-pa)
 * @Date : 2025. 04. 17.
 */
package table.eat.now.reservation.reservation.application.event.listener;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import table.eat.now.reservation.reservation.application.event.event.CancelReservationAfterCommitEvent;
import table.eat.now.reservation.reservation.application.event.event.ReservationCancelledEvent;
import table.eat.now.reservation.reservation.application.event.payload.ReservationCancelledPayload;
import table.eat.now.reservation.reservation.application.event.publisher.ReservationEventPublisher;

@Component
@RequiredArgsConstructor
public class CancelReservationEventListener {

  private final ReservationEventPublisher reservationEventPublisher;

  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void handleCancelReservationAfterCommitEvent(
      final CancelReservationAfterCommitEvent event) {
    reservationEventPublisher.publish(
        ReservationCancelledEvent.from(ReservationCancelledPayload.from(event))
    );
  }
}
