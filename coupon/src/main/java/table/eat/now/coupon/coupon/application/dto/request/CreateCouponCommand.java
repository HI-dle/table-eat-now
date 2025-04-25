package table.eat.now.coupon.coupon.application.dto.request;

import java.time.LocalDateTime;
import lombok.Builder;
import table.eat.now.coupon.coupon.domain.entity.Coupon;
import table.eat.now.coupon.coupon.domain.entity.DiscountPolicy;

@Builder
public record CreateCouponCommand(
  String name,
  String type,
  String label,
  LocalDateTime issueStartAt,
  LocalDateTime issueEndAt,
  LocalDateTime expireAt,
  Integer validDays,
  Integer count,
  Boolean allowDuplicate,
  Integer minPurchaseAmount,
  Integer amount,
  Integer percent,
  Integer maxDiscountAmount
) {

  public Coupon toEntity() {

    Coupon coupon = Coupon.of(name, type, label, issueStartAt, issueEndAt, expireAt, validDays, count, allowDuplicate);
    DiscountPolicy policy = DiscountPolicy.of(minPurchaseAmount, amount, percent, maxDiscountAmount);
    coupon.registerPolicy(policy);
    return coupon;
  }
}
