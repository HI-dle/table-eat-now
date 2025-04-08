package table.eat.now.coupon.coupon.domain.repository;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.Query;
import table.eat.now.coupon.coupon.domain.entity.Coupon;

public interface CouponRepository {

  Coupon save(Coupon coupon);

  @Query("select c from Coupon c join fetch c.policy where c.couponUuid = :couponUuid and c.deletedAt is null")
  Optional<Coupon> findByCouponUuidAndDeletedAtIsNullFetchJoin(UUID couponUuid);
}
