package table.eat.now.coupon.coupon.infrastructure.persistence.redis;

import static table.eat.now.coupon.coupon.infrastructure.persistence.redis.constant.CouponCacheConstant.COUPON_CACHE;
import static table.eat.now.coupon.coupon.infrastructure.persistence.redis.constant.CouponCacheConstant.COUPON_USER_SET;
import static table.eat.now.coupon.coupon.infrastructure.persistence.redis.constant.CouponCacheConstant.DAILY_ISSUABLE_HOT_COUPON_INDEX;
import static table.eat.now.coupon.coupon.infrastructure.persistence.redis.constant.CouponCacheConstant.DAILY_ISSUABLE_PROMO_COUPON_INDEX;

import com.fasterxml.jackson.core.type.TypeReference;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Repository;
import table.eat.now.common.exception.CustomException;
import table.eat.now.coupon.coupon.application.utils.MapperProvider;
import table.eat.now.coupon.coupon.application.utils.TimeProvider;
import table.eat.now.coupon.coupon.domain.command.CouponCachingAndIndexing;
import table.eat.now.coupon.coupon.domain.command.CouponIssuance;
import table.eat.now.coupon.coupon.domain.entity.Coupon;
import table.eat.now.coupon.coupon.domain.entity.CouponLabel;
import table.eat.now.coupon.coupon.infrastructure.persistence.redis.constant.CouponCacheConstant;
import table.eat.now.coupon.coupon.infrastructure.persistence.redis.constant.CouponLuaResultConstant.IssueResult;

@Slf4j
@Repository
@SuppressWarnings("unchecked")
public class RedisCouponCacheManager {

  private static final String COUNT_PREFIX = "coupon:count:";

  private final RedisTemplate<String, Object> redisTemplate;

  public RedisCouponCacheManager(
      @Qualifier("couponRedisTemplate")
      RedisTemplate<String, Object> couponRedisTemplate) {
    this.redisTemplate = couponRedisTemplate;
  }

  public Coupon getCouponCache(String couponUuid) {
    String cacheKey = COUPON_CACHE + couponUuid;
    Map<Object, Object> couponMap = redisTemplate.opsForHash().entries(cacheKey);
    if (couponMap.isEmpty()) {
      return null;
    }
    return MapperProvider.convertValue(couponMap, Coupon.class);
  }

  public void putCouponCache(String couponUuid, Coupon coupon, Duration ttl) {
    String cacheKey = COUPON_CACHE + couponUuid;

    redisTemplate.opsForHash().putAll(
        cacheKey,
        MapperProvider.convertValue(coupon, new TypeReference<>() {}));
    redisTemplate.expire(cacheKey, ttl);
  }

  public void putCouponCache(String couponUuid, Coupon coupon) {
    String cacheKey = COUPON_CACHE + couponUuid;

    redisTemplate.opsForHash().putAll(
        cacheKey,
        MapperProvider.convertValue(coupon, new TypeReference<>() {}));
  }

  public Long decreaseCouponCount(String key, String field) {
    return redisTemplate.opsForHash().increment(key, field, -1);
  }

  public void pipelinedPutCouponsCacheAndIndex(List<CouponCachingAndIndexing> coupons) {
    List<Object> result = redisTemplate.executePipelined(new SessionCallback<> () {
          @Override
          public Object execute(RedisOperations operations) {

            String hotIndexKey = DAILY_ISSUABLE_HOT_COUPON_INDEX + TimeProvider.getToday();
            String promoIndexKey = DAILY_ISSUABLE_PROMO_COUPON_INDEX + TimeProvider.getToday();

            for (CouponCachingAndIndexing coupon : coupons) {
              String cacheKey = COUPON_CACHE + coupon.couponUuid();
              operations.opsForHash().putAll(cacheKey, coupon.couponMap());
              operations.opsForZSet().add(
                  coupon.label() == CouponLabel.PROMOTION ? promoIndexKey : hotIndexKey,
                  cacheKey,
                  coupon.expiredAt());
            }
            return null;
          }
    });

    for (int i = 0; i < result.size(); i++) {
      Boolean success = (Boolean) result.get(i);
      if (success == null || !success) {
        log.error("REDIS Pipeline: 개별 쿠폰 캐시 입력 실패: {}", coupons.get(i).couponUuid());
      }
    }
  }

  public List<Coupon> getIssuableCouponsCacheIn(CouponLabel couponLabel) {

    String indexKey = null;

    if (couponLabel == CouponLabel.PROMOTION) {
      indexKey = CouponCacheConstant.DAILY_ISSUABLE_PROMO_COUPON_INDEX + TimeProvider.getToday();
    }
    if (couponLabel == CouponLabel.HOT) {
      indexKey = DAILY_ISSUABLE_HOT_COUPON_INDEX + TimeProvider.getToday();
    }
    return this.getCouponsCacheBy(indexKey);
  }

  public List<Coupon> getCouponsCacheBy(String indexKey) {

    List<Object> couponKeys = Objects.requireNonNull(
        redisTemplate.opsForZSet().range(indexKey, 0, -1)).stream().toList();

    if (couponKeys == null || couponKeys.isEmpty()) {
      return Collections.emptyList();
    }
    List<Object> results = redisTemplate.executePipelined(new SessionCallback<> () {
      @Override
      public Object execute(RedisOperations operations) {
        for (Object couponKey : couponKeys) {
          operations.opsForHash().entries(couponKey);
        }
        return null;
      }
    });

    List<Coupon> couponList = new ArrayList<>();
    for (int i = 0; i < results.size(); i++) {
      Map<Object, Object> couponData = (Map<Object, Object>) results.get(i);
      if (couponData != null && !couponData.isEmpty()) {
        couponList.add(MapperProvider.convertValue(couponData, Coupon.class));
      } else {
        log.warn("REDIS Pipeline: 개별 쿠폰 캐시 조회 실패: {}", couponKeys.get(i)); // todo. 캐시 조회 실패시, 디비 조회 필요
      }
    }
    return couponList;
  }

  public void requestIssueByLua(CouponIssuance command) {

    String userSetKey = COUPON_USER_SET + command.couponUuid();;
    String couponKey = COUPON_CACHE + command.couponUuid();
    String idempotencyKey = new StringBuilder().append(COUPON_CACHE)
        .append(command.couponUuid())
        .append("-")
        .append(command.userId())
        .append("-")
        .append(command.timestamp())
        .toString();
    // todo. 현재 idempotency 키가 명확하지 않음. 리팩토링 가능

    List<String> keys = List.of(userSetKey, couponKey, idempotencyKey);
    List<String> args = List.of(command.userId().toString());

    Long result = executeLuaScript(
        LuaScriptType.LIMITED_NONDUP,
        keys,
        args,
        Long.class);

    if (result != 1) {
      throw CustomException.from(IssueResult.parseToErrorCode(result));
    }
  }

  private <T> T executeLuaScript(
      LuaScriptType luaScriptType, List<String> keys, List<String> args, Class<T> resultType) {
    RedisScript<?> redisScript = luaScriptType.getRedisScript();
    Object result = redisTemplate.execute(redisScript, keys, args);

    return resultType.cast(result);
  }

  public void setCouponCountWithTtl(String couponUuid, Integer value, Duration ttl) {
    redisTemplate.opsForValue().set(COUNT_PREFIX + couponUuid, value, ttl);
  }

  public void setCouponSetWithTtl(String couponUuid, Duration ttl) {

    redisTemplate.opsForSet().add(COUPON_USER_SET + couponUuid, "INIT");
    redisTemplate.expire(COUPON_USER_SET + couponUuid, ttl);
  }

  public Long decreaseCouponCount(String couponUuid) {
    return redisTemplate.opsForValue().decrement(COUNT_PREFIX + couponUuid);
  }

  public boolean isAlreadyIssued(String couponUuid, Long userId) {
    return Boolean.TRUE.equals(
        redisTemplate.opsForSet().isMember(COUPON_USER_SET + couponUuid, userId));
  }

  public boolean markAsIssued(String couponUuid, Long userId) {
    Long addedCount = redisTemplate.opsForSet().add(COUPON_USER_SET + couponUuid, userId);
    return addedCount != null && addedCount > 0;
  }

  public Long increaseCouponCount(String couponUuid) {
    return redisTemplate.opsForValue().increment(COUNT_PREFIX + couponUuid);
  }

  public Integer getCouponCount(String couponUuid) {
    return (Integer) redisTemplate.opsForValue().get(COUNT_PREFIX + couponUuid);
  }
}
