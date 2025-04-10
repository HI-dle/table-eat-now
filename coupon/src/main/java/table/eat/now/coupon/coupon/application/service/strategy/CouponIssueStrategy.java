package table.eat.now.coupon.coupon.application.service.strategy;

import table.eat.now.coupon.coupon.domain.entity.Coupon;

public interface CouponIssueStrategy {
  boolean support(Coupon coupon);
  void issue(String couponUuid, Long userId);

}
