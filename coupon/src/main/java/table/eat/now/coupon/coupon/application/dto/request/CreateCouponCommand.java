package table.eat.now.coupon.coupon.application.dto.request;

import java.time.LocalDateTime;
import lombok.Builder;
import table.eat.now.coupon.coupon.domain.entity.Coupon;
import table.eat.now.coupon.coupon.domain.entity.DiscountPolicy;
import table.eat.now.coupon.coupon.domain.entity.CouponType;

@Builder
public record CreateCouponCommand(
  String name,
  String type,
  LocalDateTime startAt,
  LocalDateTime endAt,
  Integer count,
  Boolean allowDuplicate,
  Integer minPurchaseAmount,
  Integer amount,
  Integer percent,
  Integer maxDiscountAmount
) {

  public Coupon toEntity() {

    Coupon coupon = Coupon.of(name, CouponType.valueOf(type), startAt, endAt, count, allowDuplicate);
    DiscountPolicy policy = DiscountPolicy.of(minPurchaseAmount, amount, percent, maxDiscountAmount);
    coupon.registerPolicy(policy);
    return coupon;
  }
}
