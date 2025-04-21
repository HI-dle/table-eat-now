package table.eat.now.payment.payment.application.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import table.eat.now.payment.payment.domain.entity.Payment;

public record ReservationPaymentFailedPayload(
    String paymentUuid,
    String paymentKey,
    String cancelReason,
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    LocalDateTime cancelledAt,
    String paymentStatus
) {
  public static ReservationPaymentFailedPayload from(Payment payment, String cancelReason) {
    return new ReservationPaymentFailedPayload(
        payment.getIdentifier().getPaymentUuid(),
        payment.getPaymentKey(),
        cancelReason,
        payment.getCancelledAt(),
        payment.getPaymentStatus().name()
    );
  }
}
