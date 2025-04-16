package table.eat.now.payment.payment.infrastructure.kafka.event;

import table.eat.now.payment.payment.application.dto.request.CancelPaymentCommand;

public record ReservationCancelingPayload(
    String reservationUuid,
    String idempotencyKey,
    String cancelReason
) {

  public CancelPaymentCommand toCommand() {
    return new CancelPaymentCommand(reservationUuid, idempotencyKey, cancelReason);
  }
}
