package table.eat.now.payment.payment.application.event;

import java.time.LocalDateTime;
import table.eat.now.payment.payment.domain.entity.Payment;

public record PaymentCanceledPayload(
    String idempotencyKey,
    String paymentUuid,
    String paymentStatus,
    LocalDateTime cancelledAt
) {
  public static PaymentCanceledPayload from(Payment payment) {
    return new PaymentCanceledPayload(
        payment.getIdentifier().getIdempotencyKey(),
        payment.getIdentifier().getPaymentUuid(),
        payment.getPaymentStatus().name(),
        payment.getCancelledAt()
    );
  }
}
