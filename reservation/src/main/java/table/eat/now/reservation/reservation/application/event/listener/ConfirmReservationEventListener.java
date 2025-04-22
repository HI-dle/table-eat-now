/**
 * @author : jieun(je-pa)
 * @Date : 2025. 04. 23.
 */
package table.eat.now.reservation.reservation.application.event.listener;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import table.eat.now.reservation.reservation.application.event.event.ConfirmReservationAfterCommitEvent;
import table.eat.now.reservation.reservation.application.event.event.ReservationConfirmedEvent;
import table.eat.now.reservation.reservation.application.event.payload.ReservationConfirmedPayload;
import table.eat.now.reservation.reservation.application.event.publisher.ReservationEventPublisher;

@Component
@RequiredArgsConstructor
public class ConfirmReservationEventListener {
  private final ReservationEventPublisher reservationEventPublisher;

  // todo: 리펙토링..
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void handleConfirmReservationAfterCommitEvent(
      final ConfirmReservationAfterCommitEvent event) {
    reservationEventPublisher.publish(
        ReservationConfirmedEvent.from(ReservationConfirmedPayload.from(event))
    );
  }
}
