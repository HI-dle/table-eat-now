package table.eat.now.payment.payment.presentation.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Builder;
import table.eat.now.payment.payment.application.dto.response.ConfirmPaymentInfo;

@Builder
public record ConfirmPaymentResponse(
    String paymentUuid,
    Long customerId,
    String reservationId,
    String reservationName,
    String paymentStatus,
    BigDecimal originalAmount,
    BigDecimal discountAmount,
    BigDecimal totalAmount,
    LocalDateTime createdAt,
    LocalDateTime approvedAt,
    LocalDateTime cancelledAt
) {

  public static ConfirmPaymentResponse from(ConfirmPaymentInfo info) {
    return ConfirmPaymentResponse.builder()
        .paymentUuid(info.paymentUuid())
        .customerId(info.customerId())
        .reservationId(info.reservationId())
        .reservationName(info.reservationName())
        .paymentStatus(info.paymentStatus())
        .originalAmount(info.originalAmount())
        .discountAmount(info.discountAmount())
        .totalAmount(info.totalAmount())
        .createdAt(info.createdAt())
        .approvedAt(info.approvedAt())
        .cancelledAt(info.cancelledAt())
        .build();
  }
}
