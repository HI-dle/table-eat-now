package table.eat.now.payment.payment.infrastructure.kafka.event;

import java.time.LocalDateTime;

public record PaymentFailedPayload(
    String paymentUuid,
    String paymentKey,
    String cancelReason,
    LocalDateTime cancelledAt,
    String paymentStatus
) {
}
