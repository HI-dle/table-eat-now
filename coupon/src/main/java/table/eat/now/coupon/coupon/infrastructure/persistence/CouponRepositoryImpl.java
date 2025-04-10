package table.eat.now.coupon.coupon.infrastructure.persistence;

import java.time.Duration;
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
import table.eat.now.coupon.coupon.domain.repository.CouponRepository;
import table.eat.now.coupon.coupon.infrastructure.persistence.jpa.JpaCouponRepository;
import table.eat.now.coupon.coupon.infrastructure.persistence.redis.RedisCouponRepository;

@RequiredArgsConstructor
@Repository
public class CouponRepositoryImpl implements CouponRepository {
  private final RedisCouponRepository redisRepository;
  private final JpaCouponRepository jpaRepository;

  @Override
  public Coupon save(Coupon coupon) {
    return jpaRepository.save(coupon);
  }

  @Override
  public <S extends Coupon> List<S> saveAll(Iterable<S> coupons) {
    return jpaRepository.saveAll(coupons);
  }

  @Override
  public Optional<Coupon> findByCouponUuidAndDeletedAtIsNullFetchJoin(String couponUuid) {
    return jpaRepository.findByCouponUuidAndDeletedAtIsNullFetchJoin(couponUuid);
  }

  @Override
  public List<Coupon> findByCouponUuidsInAndDeletedAtIsNullFetchJoin(Set<String> couponUuids) {
    return jpaRepository.findByCouponUuidsInAndDeletedAtIsNullFetchJoin(couponUuids);
  }

  @Override
  public List<Coupon> findCouponsStartInFromTo(LocalDateTime fromAt, LocalDateTime toAt) {
    return jpaRepository.findCouponsStartInFromTo(fromAt, toAt);
  }

  @Override
  public Page<Coupon> searchCouponByPageableAndCondition(Pageable pageable,
      CouponCriteria criteria) {
    return jpaRepository.searchCouponByPageableAndCondition(pageable, criteria);
  }

  @Override
  public void setCouponCountWithTtl(String couponUuid, Integer value, Duration ttl) {
    redisRepository.setCouponCountWithTtl(couponUuid, value, ttl);
  }

  @Override
  public void setCouponSetWithTtl(String couponUuid, Duration ttl) {
    redisRepository.setCouponSetWithTtl(couponUuid, ttl);
  }

  @Override
  public Long decreaseCouponCount(String couponUuid) {
    return redisRepository.decreaseCouponCount(couponUuid);
  }

  @Override
  public boolean isAlreadyIssued(String couponUuid, Long userId) {
    return redisRepository.isAlreadyIssued(couponUuid, userId);
  }

  @Override
  public boolean markAsIssued(String couponUuid, Long userId) {
    return redisRepository.markAsIssued(couponUuid, userId);
  }

  @Override
  public Long increaseCouponCount(String couponUuid) {
    return redisRepository.increaseCouponCount(couponUuid);
  }
}
