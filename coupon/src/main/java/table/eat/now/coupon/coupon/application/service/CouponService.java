package table.eat.now.coupon.coupon.application.service;

import java.util.UUID;
import table.eat.now.coupon.coupon.application.dto.request.CreateCouponCommand;
import table.eat.now.coupon.coupon.application.dto.request.UpdateCouponCommand;

public interface CouponService {

  String createCoupon(CreateCouponCommand command);

  void updateCoupon(UUID couponUuid, UpdateCouponCommand command);
}
