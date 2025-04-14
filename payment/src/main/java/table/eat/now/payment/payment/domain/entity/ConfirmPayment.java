package table.eat.now.payment.payment.domain.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ConfirmPayment(
    String paymentKey,
    BigDecimal discountAmount,
    BigDecimal totalAmount,
    LocalDateTime approvedAt
) {

}
