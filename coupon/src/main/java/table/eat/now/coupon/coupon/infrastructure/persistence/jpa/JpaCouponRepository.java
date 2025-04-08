package table.eat.now.coupon.coupon.infrastructure.persistence.jpa;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import table.eat.now.coupon.coupon.domain.entity.Coupon;
import table.eat.now.coupon.coupon.domain.repository.CouponRepository;

public interface JpaCouponRepository
    extends JpaRepository<Coupon, Long>, CouponRepositoryCustom, CouponRepository {

  @Query("select c from Coupon c join fetch c.policy "
      + "where c.couponUuid = :couponUuid and c.deletedAt is null")
  Optional<Coupon> findByCouponUuidAndDeletedAtIsNullFetchJoin(String couponUuid);
}
