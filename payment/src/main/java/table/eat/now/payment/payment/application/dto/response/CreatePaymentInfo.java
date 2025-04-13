package table.eat.now.payment.payment.application.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Builder;
import table.eat.now.payment.payment.domain.entity.Payment;

@Builder
public record CreatePaymentInfo(
    String paymentUuid,
    String idempotencyKey,
    String paymentStatus,
    BigDecimal originalAmount,
    LocalDateTime createdAt
) {

  public static CreatePaymentInfo from(Payment payment) {
    return CreatePaymentInfo.builder()
        .paymentUuid(payment.getIdentifier().getPaymentUuid())
        .idempotencyKey(payment.getIdentifier().getIdempotencyKey())
        .paymentStatus(payment.getPaymentStatus().name())
        .originalAmount(payment.getAmount().getOriginalAmount())
        .createdAt(payment.getCreatedAt())
        .build();
  }
}