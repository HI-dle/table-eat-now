package table.eat.now.coupon.coupon.infrastructure.persistence;

import java.time.Duration;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import table.eat.now.coupon.coupon.domain.command.CouponCachingAndIndexing;
import table.eat.now.coupon.coupon.domain.command.CouponIssuance;
import table.eat.now.coupon.coupon.domain.entity.Coupon;
import table.eat.now.coupon.coupon.domain.store.CouponStore;
import table.eat.now.coupon.coupon.infrastructure.persistence.jpa.JpaCouponRepository;
import table.eat.now.coupon.coupon.infrastructure.persistence.redis.RedisCouponCacheManager;

@RequiredArgsConstructor
@Repository
public class CouponStoreImpl implements CouponStore {
  private final RedisCouponCacheManager redisCacheManager;
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
  public void setCouponCountWithTtl(String couponUuid, Integer value, Duration ttl) {
    redisCacheManager.setCouponCountWithTtl(couponUuid, value, ttl);
  }

  @Override
  public void setCouponSetWithTtl(String couponUuid, Duration ttl) {
    redisCacheManager.setCouponSetWithTtl(couponUuid, ttl);
  }

  @Override
  public Long decreaseCouponCount(String key) {
    return redisCacheManager.decreaseCouponCount(key);
  }

  @Override
  public boolean markAsIssued(String couponUuid, Long userId) {
    return redisCacheManager.markAsIssued(couponUuid, userId);
  }

  @Override
  public Long increaseCouponCount(String couponUuid) {
    return redisCacheManager.increaseCouponCount(couponUuid);
  }

  @Override
  public void insertCouponCache(String couponUuid, Coupon coupon, Duration ttl) {
    redisCacheManager.putCouponCache(couponUuid, coupon, ttl);
  }

  @Override
  public void updateCouponCache(String couponUuid, Coupon coupon) {
    redisCacheManager.putCouponCache(couponUuid, coupon);
  }

  @Override
  public void insertCouponsCacheAndSubIndex(List<CouponCachingAndIndexing> couponInfos) {
    redisCacheManager.pipelinedPutCouponsCacheAndIndex(couponInfos);
  }

  @Override
  public void requestIssue(CouponIssuance command) {
    redisCacheManager.requestIssueByLua(command);
  }
}
