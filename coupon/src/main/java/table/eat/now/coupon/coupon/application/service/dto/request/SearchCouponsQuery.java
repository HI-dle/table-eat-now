package table.eat.now.coupon.coupon.application.service.dto.request;

import java.time.LocalDateTime;
import lombok.Builder;
import table.eat.now.coupon.coupon.domain.criteria.CouponCriteria;

@Builder
public record SearchCouponsQuery(
    LocalDateTime fromAt,
    LocalDateTime toAt,
    String type
) {

  public CouponCriteria toCriteria() {
    return CouponCriteria.builder()
        .fromAt(fromAt)
        .toAt(toAt)
        .type(type)
        .build();
  }
}
