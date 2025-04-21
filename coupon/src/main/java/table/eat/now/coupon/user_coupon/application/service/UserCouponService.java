package table.eat.now.coupon.user_coupon.application.service;

import org.springframework.data.domain.Pageable;
import table.eat.now.common.resolver.dto.CurrentUserInfoDto;
import table.eat.now.coupon.user_coupon.application.dto.request.IssueUserCouponCommand;
import table.eat.now.coupon.user_coupon.application.dto.request.PreemptUserCouponCommand;
import table.eat.now.coupon.user_coupon.application.dto.response.GetUserCouponInfo;
import table.eat.now.coupon.user_coupon.application.dto.response.PageResponse;

public interface UserCouponService {

  void createUserCoupon(IssueUserCouponCommand command);

  void preemptUserCoupon(CurrentUserInfoDto userInfoDto, PreemptUserCouponCommand command);

  void preemptUserCouponWithDistributedLock(
      CurrentUserInfoDto userInfoDto, PreemptUserCouponCommand command);

  PageResponse<GetUserCouponInfo> getUserCouponsByUserId(
      CurrentUserInfoDto userInfoDto, Pageable pageable);
}
