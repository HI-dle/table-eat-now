package table.eat.now.coupon.coupon.infrastructure.persistence.jpa;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
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
      + "where c.period.issueStartAt >= :fromAt and c.period.issueStartAt < :toAt "
      + "and c.label in ('HOT', 'PROMOTION')"
      + "and c.deletedAt is null")
  List<Coupon> findCouponsByIssueStartAtBtwAndHotPromo(LocalDateTime fromAt, LocalDateTime toAt);

  @Modifying
  @Query("update Coupon c set c.issuedCount = :issuedCount, c.version = c.version + 1 "
      + "where c.couponUuid = :couponUuid and c.version <= :version")
  void updateIssuedCount(String couponUuid, Integer issuedCount, Long version);
}
