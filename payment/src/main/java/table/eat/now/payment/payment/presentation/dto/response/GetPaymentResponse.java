package table.eat.now.payment.payment.presentation.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Builder;
import table.eat.now.payment.payment.application.dto.response.GetPaymentInfo;

@Builder
public record GetPaymentResponse(
    String paymentUuid,
    Long customerId,
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
  public static GetPaymentResponse from(GetPaymentInfo info){
    return GetPaymentResponse.builder()
        .paymentUuid(info.paymentUuid())
        .customerId(info.customerId())
        .reservationId(info.reservationId())
        .restaurantId(info.restaurantId())
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
