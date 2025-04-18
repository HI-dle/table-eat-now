package table.eat.now.payment.payment.presentation.dto.request;

import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDate;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import table.eat.now.payment.payment.application.dto.request.SearchMasterPaymentsQuery;

public record SearchMasterPaymentsRequest(
    UUID restaurantUuid,
    Long userId,
    @Pattern(regexp = "^(PENDING|APPROVED|CANCELED|REFUNDED)$") String paymentStatus,
    @PastOrPresent LocalDate startDate,
    @PastOrPresent LocalDate endDate,
    @Pattern(regexp = "^(createdAt|updatedAt)$") String orderBy,
    @Pattern(regexp = "^(desc|asc)$") String sort
) {

  public SearchMasterPaymentsRequest {
    orderBy = orderBy != null ? orderBy : "createdAt";
    sort = sort != null ? sort : "desc";
  }

  public SearchMasterPaymentsQuery toQuery(Pageable pageable) {
    return SearchMasterPaymentsQuery.builder()
        .restaurantUuid(restaurantUuid != null ? restaurantUuid.toString() : null)
        .paymentStatus(paymentStatus)
        .startDate(startDate)
        .endDate(endDate)
        .orderBy(orderBy)
        .sort(sort)
        .page(pageable.getPageNumber())
        .size(pageable.getPageSize())
        .userId(userId)
        .build();
  }
}
