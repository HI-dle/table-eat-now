package table.eat.now.payment.payment.infrastructure.kafka.event;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record PaymentSuccessPayload(
    String paymentUuid,
    String idempotencyKey,
    String paymentStatus,
    BigDecimal originalAmount,
    BigDecimal discountAmount,
    BigDecimal totalAmount,
    LocalDateTime createdAt,
    LocalDateTime approvedAt
) {
}
