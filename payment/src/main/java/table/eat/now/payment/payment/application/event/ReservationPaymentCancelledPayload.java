package table.eat.now.payment.payment.application.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import table.eat.now.payment.payment.domain.entity.Payment;

public record ReservationPaymentCancelledPayload(
    String idempotencyKey,
    String paymentUuid,
    String paymentStatus,
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    LocalDateTime cancelledAt
) {
  public static ReservationPaymentCancelledPayload from(Payment payment) {
    return new ReservationPaymentCancelledPayload(
        payment.getIdentifier().getIdempotencyKey(),
        payment.getIdentifier().getPaymentUuid(),
        payment.getPaymentStatus().name(),
        payment.getCancelledAt()
    );
  }
}
