package table.eat.now.payment.payment.domain.entity;

import java.time.LocalDateTime;

public record CancelPayment(
    String paymentKey,
    String cancelReason,
    LocalDateTime cancelledAt
) {
}
