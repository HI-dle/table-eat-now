package table.eat.now.payment.payment.application.event;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Builder;
import table.eat.now.payment.payment.domain.entity.Payment;

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

  public static PaymentSuccessPayload from(Payment payment) {
    return PaymentSuccessPayload.builder()
        .paymentUuid(payment.getIdentifier().getPaymentUuid())
        .idempotencyKey(payment.getIdentifier().getIdempotencyKey())
        .paymentStatus(payment.getPaymentStatus().name())
        .originalAmount(payment.getAmount().getOriginalAmount())
        .discountAmount(payment.getAmount().getDiscountAmount())
        .totalAmount(payment.getAmount().getTotalAmount())
        .createdAt(payment.getCreatedAt())
        .approvedAt(payment.getApprovedAt())
        .build();
  }
}
