package table.eat.now.coupon.coupon.infrastructure.persistence.redis;

import java.time.Duration;

public interface RedisCouponRepository {

  void setCouponCountWithTtl(String couponUuid, Integer value, Duration ttl);

  void setCouponSetWithTtl(String couponUuid, Duration ttl);

  boolean decreaseCouponCount(String couponUuid);

  boolean isAlreadyIssued(String couponUuid, Long userId);

  boolean markAsIssued(String couponUuid, Long userId);

  boolean increaseCouponCount(String couponUuid);
}
