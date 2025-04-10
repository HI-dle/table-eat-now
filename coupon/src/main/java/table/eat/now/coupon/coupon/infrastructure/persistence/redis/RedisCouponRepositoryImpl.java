package table.eat.now.coupon.coupon.infrastructure.persistence.redis;

import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class RedisCouponRepositoryImpl implements RedisCouponRepository {
  private static final String COUNT_PREFIX = "coupon:count:";
  private static final String USER_SET_PREFIX = "coupon:user:";
  private final RedisTemplate<String, Object> redisTemplate;

  @Override
  public void setCouponCountWithTtl(String couponUuid, Integer value, Duration ttl) {
    redisTemplate.opsForValue().set(COUNT_PREFIX + couponUuid, value, ttl);
  }

  @Override
  public void setCouponSetWithTtl(String couponUuid, Duration ttl) {
    redisTemplate.opsForSet().add(USER_SET_PREFIX + couponUuid, "INIT");
    redisTemplate.expire(USER_SET_PREFIX + couponUuid, ttl);
  }

  @Override
  public boolean decreaseCouponCount(String couponUuid) {
    Long stock = redisTemplate.opsForValue().decrement(COUNT_PREFIX + couponUuid);
    return stock != null && stock >= 0;
  }

  @Override
  public boolean isAlreadyIssued(String couponUuid, Long userId) {
    return Boolean.TRUE.equals(
        redisTemplate.opsForSet().isMember(USER_SET_PREFIX + couponUuid, userId));
  }

  @Override
  public boolean markAsIssued(String couponUuid, Long userId) {
    Long addedCount = redisTemplate.opsForSet().add(USER_SET_PREFIX + couponUuid, userId);
    return addedCount != null && addedCount > 0;
  }

  @Override
  public boolean increaseCouponCount(String couponUuid) {
    Long stock = redisTemplate.opsForValue().increment(COUNT_PREFIX + couponUuid);
    return stock != null;
  }
}
