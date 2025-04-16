package table.eat.now.payment.payment.application.event;

import java.time.LocalDateTime;
import table.eat.now.payment.payment.domain.entity.Payment;

public record PaymentFailedPayload(
    String paymentUuid,
    String paymentKey,
    String cancelReason,
    LocalDateTime cancelledAt,
    String paymentStatus
) {
  public static PaymentFailedPayload from(Payment payment, String cancelReason) {
    return new PaymentFailedPayload(
        payment.getIdentifier().getPaymentUuid(),
        payment.getPaymentKey(),
        cancelReason,
        payment.getCancelledAt(),
        payment.getPaymentStatus().name()
    );
  }
}
