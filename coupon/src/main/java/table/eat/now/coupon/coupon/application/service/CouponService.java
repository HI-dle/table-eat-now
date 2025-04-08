package table.eat.now.coupon.coupon.application.service;

import java.util.UUID;
import table.eat.now.coupon.coupon.application.dto.request.CreateCouponCommand;

public interface CouponService {

  UUID createCoupon(CreateCouponCommand command);
}
