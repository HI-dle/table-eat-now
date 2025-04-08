package table.eat.now.coupon.coupon.domain.repository;

import java.util.Optional;
import table.eat.now.coupon.coupon.domain.entity.Coupon;

public interface CouponRepository {

  Coupon save(Coupon coupon);

  Optional<Coupon> findByCouponUuidAndDeletedAtIsNullFetchJoin(String couponUuid);
}
