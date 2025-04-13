package table.eat.now.payment.payment.presentation.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Builder;
import table.eat.now.payment.payment.application.dto.response.CreatePaymentInfo;

@Builder
public record CreatePaymentResponse(
    String paymentUuid,
    String idempotencyKey,
    String paymentStatus,
    BigDecimal originalAmount,
    LocalDateTime createdAt
) {

  public static CreatePaymentResponse from(CreatePaymentInfo info) {
    return CreatePaymentResponse.builder()
        .paymentUuid(info.paymentUuid())
        .idempotencyKey(info.idempotencyKey())
        .paymentStatus(info.paymentStatus())
        .originalAmount(info.originalAmount())
        .createdAt(info.createdAt())
        .build();
  }
}
