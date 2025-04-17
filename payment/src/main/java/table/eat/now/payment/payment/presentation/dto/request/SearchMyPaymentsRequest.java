package table.eat.now.payment.payment.presentation.dto.request;

import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDate;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import table.eat.now.common.resolver.dto.CurrentUserInfoDto;
import table.eat.now.payment.payment.application.dto.request.SearchMyPaymentsQuery;

public record SearchMyPaymentsRequest(
    UUID restaurantUuid,
    @Pattern(regexp = "^(PENDING|APPROVED|CANCELED|REFUNDED)$") String paymentStatus,
    @PastOrPresent LocalDate startDate,
    @PastOrPresent LocalDate endDate,
    @Pattern(regexp = "^(createdAt|updatedAt)$") String orderBy,
    @Pattern(regexp = "^(desc|asc)$") String sort
) {

  public SearchMyPaymentsRequest {
    orderBy = orderBy != null ? orderBy : "createdAt";
    sort = sort != null ? sort : "desc";
  }

  public SearchMyPaymentsQuery toQuery(Pageable pageable, CurrentUserInfoDto userInfo) {
    return SearchMyPaymentsQuery.builder()
        .restaurantUuid(restaurantUuid != null ? restaurantUuid.toString() : null)
        .paymentStatus(paymentStatus)
        .startDate(startDate)
        .endDate(endDate)
        .orderBy(orderBy)
        .sort(sort)
        .page(pageable.getPageNumber())
        .size(pageable.getPageSize())
        .userId(userInfo.userId())
        .build();
  }
}
