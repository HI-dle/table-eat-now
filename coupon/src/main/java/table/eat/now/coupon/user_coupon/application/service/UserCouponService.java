package table.eat.now.coupon.user_coupon.application.service;

import table.eat.now.common.resolver.dto.CurrentUserInfoDto;
import table.eat.now.coupon.user_coupon.application.dto.request.IssueUserCouponCommand;
import table.eat.now.coupon.user_coupon.application.dto.request.PreemptUserCouponCommand;

public interface UserCouponService {

  void createUserCoupon(IssueUserCouponCommand command);

  void preemptUserCoupon(CurrentUserInfoDto userInfoDto, String userCouponUuid, PreemptUserCouponCommand command);
}
