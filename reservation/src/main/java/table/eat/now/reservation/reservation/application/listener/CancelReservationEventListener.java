/**
 * @author : jieun(je-pa)
 * @Date : 2025. 04. 17.
 */
package table.eat.now.reservation.reservation.application.listener;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import table.eat.now.reservation.reservation.application.listener.event.CancelReservationAfterCommitEvent;

@Component
@RequiredArgsConstructor
public class CancelReservationEventListener {


  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void handleCancelReservationAfterCommitEvent(
      final CancelReservationAfterCommitEvent event) {
    // todo: kafka ReservationCancelEvent 이벤트 발행
  }
}
