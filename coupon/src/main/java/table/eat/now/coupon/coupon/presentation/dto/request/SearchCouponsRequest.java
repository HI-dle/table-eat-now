package table.eat.now.coupon.coupon.presentation.dto.request;

import jakarta.validation.constraints.Pattern;
import java.time.LocalDateTime;
import table.eat.now.coupon.coupon.application.dto.request.SearchCouponsQuery;

public record SearchCouponsRequest(
    LocalDateTime fromAt,
    LocalDateTime toAt,
    @Pattern(regexp = "PERCENT_DISCOUNT|FIXED_DISCOUNT") String type
) {

  public SearchCouponsQuery toQuery() {
    return SearchCouponsQuery.builder()
        .fromAt(fromAt)
        .toAt(toAt)
        .type(type)
        .build();
  }
}
