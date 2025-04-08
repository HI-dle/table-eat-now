package table.eat.now.coupon.coupon.application.service;

import java.util.UUID;
import table.eat.now.common.resolver.dto.CurrentUserInfoDto;
import table.eat.now.coupon.coupon.application.dto.request.CreateCouponCommand;
import table.eat.now.coupon.coupon.application.dto.request.UpdateCouponCommand;
import table.eat.now.coupon.coupon.application.dto.response.GetCouponInfo;

public interface CouponService {

  String createCoupon(CreateCouponCommand command);

  void updateCoupon(UUID couponUuid, UpdateCouponCommand command);

  GetCouponInfo getCoupon(UUID couponUuid);

  void deleteCoupon(CurrentUserInfoDto userInfo, UUID couponUuid);
}
