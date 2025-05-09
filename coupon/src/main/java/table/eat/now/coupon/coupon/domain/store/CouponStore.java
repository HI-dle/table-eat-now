package table.eat.now.coupon.coupon.domain.store;

import java.time.Duration;
import java.util.List;
import table.eat.now.coupon.coupon.domain.command.CouponCachingAndIndexing;
import table.eat.now.coupon.coupon.domain.command.CouponIssuance;
import table.eat.now.coupon.coupon.domain.entity.Coupon;

public interface CouponStore {

  void insertCouponCache(String couponUuid, Coupon coupon, Duration ttl);

  void updateCouponCache(String couponUuid, Coupon coupon);

  void insertCouponsCacheAndSubIndex(List<CouponCachingAndIndexing> command);

  void requestIssue(CouponIssuance domainCommand);

  Coupon save(Coupon coupon);

  <S extends Coupon> List<S> saveAll(Iterable<S> coupons);

  void setCouponCountWithTtl(String couponUuid, Integer value, Duration ttl);

  void setCouponSetWithTtl(String couponUuid, Duration ttl);

  Long decreaseCouponCount(String couponUuid);

  boolean markAsIssued(String couponUuid, Long userId);

  Long increaseCouponCount(String couponUuid);

  void updateIssuedCount(String couponUuid, Integer issuedCount, Long version);

  void deleteDirtyCouponKeysByScore(long threshold);
}
