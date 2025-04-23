/**
 * @author : jieun(je-pa)
 * @Date : 2025. 04. 23.
 */
package table.eat.now.reservation.reservation.application.event.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import table.eat.now.reservation.reservation.application.event.event.ConfirmReservationAfterCommitEvent;
import table.eat.now.reservation.reservation.application.event.event.ReservationConfirmedEvent;
import table.eat.now.reservation.reservation.application.event.payload.ReservationConfirmedPayload;
import table.eat.now.reservation.reservation.application.event.publisher.ReservationEventPublisher;

@Component
@RequiredArgsConstructor
@Slf4j
public class ConfirmReservationEventListener {

  private final ReservationEventPublisher reservationEventPublisher;

  // todo: 리펙토링..
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void handleConfirmReservationAfterCommitEvent(
      final ConfirmReservationAfterCommitEvent event) {
    try {
      reservationEventPublisher.publish(
          ReservationConfirmedEvent.from(ReservationConfirmedPayload.from(event))
      );
    } catch (Exception e) {
      log.error("예약 확정 이벤트 처리 중 오류 발생: {}", e.getMessage(), e);
    }
  }
}
