package table.eat.now.coupon.coupon.infrastructure.persistence.jpa;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import table.eat.now.coupon.coupon.domain.entity.Coupon;

public interface JpaCouponRepository
    extends JpaRepository<Coupon, Long>, CouponRepositoryCustom { //, CouponRepository {

  @Query("select c from Coupon c join fetch c.policy "
      + "where c.couponUuid = :couponUuid and c.deletedAt is null")
  Optional<Coupon> findByCouponUuidAndDeletedAtIsNullFetchJoin(String couponUuid);

  @Query("select c from Coupon c join fetch c.policy "
      + "where c.couponUuid in :couponUuids and c.deletedAt is null")
  List<Coupon> findByCouponUuidsInAndDeletedAtIsNullFetchJoin(Set<String> couponUuids);

  @Query("select c from Coupon c join fetch c.policy "
      + "where c.period.startAt >= :fromAt and c.period.startAt < :toAt "
      + "and c.deletedAt is null")
  List<Coupon> findCouponsStartInFromTo(LocalDateTime fromAt, LocalDateTime toAt);
}
