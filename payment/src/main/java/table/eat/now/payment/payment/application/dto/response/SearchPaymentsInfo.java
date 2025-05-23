package table.eat.now.payment.payment.application.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Builder;
import table.eat.now.payment.payment.domain.repository.search.SearchPaymentsResult;

@Builder
public record SearchPaymentsInfo(
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
  public static SearchPaymentsInfo from(SearchPaymentsResult result) {
    return SearchPaymentsInfo.builder()
        .paymentUuid(result.paymentUuid())
        .customerId(result.customerId())
        .paymentKey(result.paymentKey())
        .reservationId(result.reservationId())
        .restaurantId(result.restaurantId())
        .reservationName(result.reservationName())
        .paymentStatus(result.paymentStatus())
        .originalAmount(result.originalAmount())
        .discountAmount(result.discountAmount())
        .totalAmount(result.totalAmount())
        .createdAt(result.createdAt())
        .approvedAt(result.approvedAt())
        .cancelledAt(result.cancelledAt())
        .build();
  }
}

