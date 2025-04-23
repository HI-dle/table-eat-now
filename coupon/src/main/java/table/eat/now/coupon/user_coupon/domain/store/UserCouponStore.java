package table.eat.now.coupon.user_coupon.domain.store;

import java.util.List;
import table.eat.now.coupon.user_coupon.domain.entity.UserCoupon;

public interface UserCouponStore {

  void optimizedSaveAll(List<UserCoupon> userCoupons);

  void batchInsert(List<UserCoupon> userCoupons);
}
