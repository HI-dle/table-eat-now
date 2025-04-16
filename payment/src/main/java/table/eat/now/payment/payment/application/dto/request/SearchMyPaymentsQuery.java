package table.eat.now.payment.payment.application.dto.request;

import java.time.LocalDate;
import lombok.Builder;
import table.eat.now.payment.payment.domain.entity.PaymentStatus;
import table.eat.now.payment.payment.domain.repository.search.SearchMyPaymentsCriteria;

@Builder
public record SearchMyPaymentsQuery(
    String restaurantUuid,
    String paymentStatus,
    LocalDate startDate,
    LocalDate endDate,
    Long userId,
    String orderBy,
    String sort,
    int page,
    int size
) {

  public SearchMyPaymentsCriteria toCriteria() {
    return SearchMyPaymentsCriteria.builder()
        .restaurantUuid(restaurantUuid)
        .paymentStatus(paymentStatus == null ? null : PaymentStatus.from(paymentStatus))
        .startDate(startDate)
        .endDate(endDate)
        .orderBy(orderBy)
        .sort(sort)
        .page(page)
        .size(size)
        .userId(userId)
        .build();
  }
}
