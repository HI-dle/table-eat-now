package table.eat.now.coupon.user_coupon.domain.repository;

import java.util.Optional;
import table.eat.now.coupon.user_coupon.domain.entity.UserCoupon;

public interface UserCouponRepository {
  UserCoupon save(UserCoupon userCoupon);

  Optional<UserCoupon> findByUserCouponUuidAndDeletedAtIsNull(String userCouponUuid);
}
