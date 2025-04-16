package table.eat.now.payment.payment.presentation.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Builder;
import table.eat.now.payment.payment.application.dto.response.SearchMyPaymentInfo;

@Builder
public record SearchMyPaymentResponse(
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

  public static SearchMyPaymentResponse from(SearchMyPaymentInfo info) {
    return SearchMyPaymentResponse.builder()
        .paymentUuid(info.paymentUuid())
        .customerId(info.customerId())
        .paymentKey(info.paymentKey())
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
