package table.eat.now.payment.payment.domain.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CancelPayment(
    String paymentKey,
    String cancelReason,
    BigDecimal cancelAmount,
    BigDecimal balanceAmount,
    LocalDateTime cancelledAt
) {
}
