package table.eat.now.payment.payment.application.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Builder;
import table.eat.now.payment.payment.domain.entity.Payment;

@Builder
public record GetPaymentInfo(
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
  public static GetPaymentInfo from(Payment payment){
    return GetPaymentInfo.builder()
        .paymentUuid(payment.getIdentifier().getPaymentUuid())
        .customerId(payment.getReference().getCustomerId())
        .reservationId(payment.getReference().getReservationId())
        .restaurantId(payment.getReference().getRestaurantId())
        .reservationName(payment.getReference().getReservationName())
        .paymentStatus(payment.getPaymentStatus().name())
        .originalAmount(payment.getAmount().getOriginalAmount())
        .discountAmount(payment.getAmount().getDiscountAmount())
        .totalAmount(payment.getAmount().getTotalAmount())
        .createdAt(payment.getCreatedAt())
        .approvedAt(payment.getApprovedAt())
        .cancelledAt(payment.getCancelledAt())
        .build();
  }
}
