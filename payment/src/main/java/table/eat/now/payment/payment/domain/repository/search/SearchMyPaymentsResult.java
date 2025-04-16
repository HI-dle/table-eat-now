package table.eat.now.payment.payment.domain.repository.search;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record SearchMyPaymentsResult(
    String paymentUuid,
    Long customerId,
    String paymentKey,
    String reservationId,
    String restaurantId,
    String reservationName,
    String paymentStatus,
    BigDecimal originalAmount,
    BigDecimal discountAmount,
    BigDecimal totalAmount,
    LocalDateTime createdAt,
    LocalDateTime approvedAt,
    LocalDateTime cancelledAt
) {

}
