package table.eat.now.payment.payment.domain.repository.search;

import java.time.LocalDate;
import lombok.Builder;
import table.eat.now.payment.payment.domain.entity.PaymentStatus;

@Builder
public record SearchPaymentsCriteria(
    String restaurantUuid,
    PaymentStatus paymentStatus,
    LocalDate startDate,
    LocalDate endDate,
    Long userId,
    String orderBy,
    String sort,
    int page,
    int size
){

}
