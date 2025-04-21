package table.eat.now.coupon.user_coupon.application.service;

import org.springframework.data.domain.Pageable;
import table.eat.now.common.resolver.dto.CurrentUserInfoDto;
import table.eat.now.coupon.user_coupon.application.dto.request.IssueUserCouponCommand;
import table.eat.now.coupon.user_coupon.application.dto.request.PreemptUserCouponCommand;
import table.eat.now.coupon.user_coupon.application.dto.response.GetUserCouponInfo;
import table.eat.now.coupon.user_coupon.application.dto.response.PageResponse;

public interface UserCouponService {

  void createUserCoupon(IssueUserCouponCommand command);

  void preemptUserCoupons(CurrentUserInfoDto userInfoDto, PreemptUserCouponCommand command);

  void preemptUserCouponsWithDistributedLock(
      CurrentUserInfoDto userInfoDto, PreemptUserCouponCommand command);

  PageResponse<GetUserCouponInfo> getUserCouponsByUserId(
      CurrentUserInfoDto userInfoDto, Pageable pageable);

  void cancelUserCoupons(String reservationUuid);
}
