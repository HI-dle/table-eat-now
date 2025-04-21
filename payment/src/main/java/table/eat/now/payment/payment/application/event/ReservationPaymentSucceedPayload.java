package table.eat.now.payment.payment.application.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Builder;
import table.eat.now.payment.payment.domain.entity.Payment;

@Builder
public record ReservationPaymentSucceedPayload(
    String paymentUuid,
    String idempotencyKey,
    String paymentStatus,
    BigDecimal originalAmount,
    BigDecimal discountAmount,
    BigDecimal totalAmount,
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    LocalDateTime createdAt,
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    LocalDateTime approvedAt
) {

  public static ReservationPaymentSucceedPayload from(Payment payment) {
    return ReservationPaymentSucceedPayload.builder()
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
