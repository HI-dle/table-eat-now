package table.eat.now.coupon.coupon.domain.command;

import java.time.LocalDateTime;
import lombok.Builder;
import table.eat.now.coupon.coupon.domain.entity.CouponType;

@Builder
public record UpdateCoupon(
    String name,
    CouponType type,
    LocalDateTime startAt,
    LocalDateTime endAt,
    Integer count,
    Boolean allowDuplicate,
    Integer minPurchaseAmount,
    Integer amount,
    Integer percent,
    Integer maxDiscountAmount
) {

}
