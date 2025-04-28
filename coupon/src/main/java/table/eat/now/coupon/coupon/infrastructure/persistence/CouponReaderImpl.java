package table.eat.now.coupon.coupon.infrastructure.persistence;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import table.eat.now.coupon.coupon.domain.criteria.CouponCriteria;
import table.eat.now.coupon.coupon.domain.entity.Coupon;
import table.eat.now.coupon.coupon.domain.entity.CouponLabel;
import table.eat.now.coupon.coupon.domain.reader.CouponReader;
import table.eat.now.coupon.coupon.infrastructure.persistence.jpa.JpaCouponRepository;
import table.eat.now.coupon.coupon.infrastructure.persistence.redis.RedisCouponCacheManager;

@RequiredArgsConstructor
@Repository
public class CouponReaderImpl implements CouponReader {
  private final JpaCouponRepository jpaRepository;
  private final RedisCouponCacheManager redisCouponCacheManager;

  @Override
  public Optional<Coupon> findValidCouponByUuid(String couponUuid) {
    Coupon couponCache = redisCouponCacheManager.getCouponCache(couponUuid);
    if (couponCache != null) {
      return Optional.of(couponCache);
    }
    return jpaRepository.findByCouponUuidAndDeletedAtIsNullFetchJoin(couponUuid);
  }

  @Override
  public List<Coupon> getValidCouponsByUuids(Set<String> couponUuids) {
    return jpaRepository.findByCouponUuidsInAndDeletedAtIsNullFetchJoin(couponUuids);
  }

  @Override
  public List<Coupon> findCouponsByIssueStartAtBtwAndHotPromo(LocalDateTime fromAt, LocalDateTime toAt) {
    return jpaRepository.findCouponsByIssueStartAtBtwAndHotPromo(fromAt, toAt);
  }

  @Override
  public Page<Coupon> searchCouponByPageableAndCondition(Pageable pageable,
      CouponCriteria criteria) {
    return jpaRepository.searchCouponByPageableAndCondition(pageable, criteria);
  }

  @Override
  public Integer getCouponCount(String couponUuid) {
    return redisCouponCacheManager.getCouponCount(couponUuid);
  }

  @Override
  public Page<Coupon> getAvailableGeneralCoupons(Pageable pageable, LocalDateTime time) {

    Page<Coupon> coupons = jpaRepository.getAvailableGeneralCoupons(pageable, time)
        .map(coupon -> {
          if (coupon.hasStockCount() && coupon.getLabel().isNotSystem()) {
            Integer remainder = redisCouponCacheManager.getCouponCount(coupon.getCouponUuid());
            coupon.calcIssuedCount(remainder);
          }
          return coupon;
        });
    return coupons;
  }

  @Override
  public List<Coupon> getIssuableCouponsCacheIn(CouponLabel couponLabel) {

    return redisCouponCacheManager.getIssuableCouponsCacheIn(couponLabel);
  }

  @Override
  public boolean isAlreadyIssued(String couponUuid, Long userId) {
    return redisCouponCacheManager.isAlreadyIssued(couponUuid, userId);
  }

}
