package table.eat.now.coupon.coupon.application.strategy;

import table.eat.now.coupon.coupon.domain.info.CouponProfile;

public interface IssueStrategy {
  void requestIssue(String couponUuid, Long userId);
  CouponProfile couponProfile();
}
