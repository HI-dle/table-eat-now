package table.eat.now.coupon.coupon.domain.criteria;

import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record CouponCriteria(
    LocalDateTime fromAt,
    LocalDateTime toAt,
    String type
) {
}
