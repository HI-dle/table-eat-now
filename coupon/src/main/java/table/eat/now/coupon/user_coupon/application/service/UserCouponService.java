package table.eat.now.coupon.user_coupon.application.service;

import table.eat.now.coupon.user_coupon.application.dto.request.IssueUserCouponCommand;

public interface UserCouponService {

  void createUserCoupon(IssueUserCouponCommand command);
}
