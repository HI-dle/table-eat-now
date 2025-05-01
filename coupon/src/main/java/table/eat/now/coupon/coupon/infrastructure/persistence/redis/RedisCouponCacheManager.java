package table.eat.now.coupon.coupon.infrastructure.persistence.redis;

import static table.eat.now.coupon.coupon.infrastructure.persistence.redis.constant.CouponCacheConstant.COUPON_CACHE;
import static table.eat.now.coupon.coupon.infrastructure.persistence.redis.constant.CouponCacheConstant.COUPON_USER_SET;
import static table.eat.now.coupon.coupon.infrastructure.persistence.redis.constant.CouponCacheConstant.DAILY_ISSUABLE_HOT_COUPON_INDEX;
import static table.eat.now.coupon.coupon.infrastructure.persistence.redis.constant.CouponCacheConstant.DAILY_ISSUABLE_PROMO_COUPON_INDEX;
import static table.eat.now.coupon.coupon.infrastructure.persistence.redis.constant.CouponCacheConstant.DIRTY_COUPON_SET;

import com.fasterxml.jackson.core.type.TypeReference;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Repository;
import table.eat.now.common.exception.CustomException;
import table.eat.now.coupon.coupon.application.utils.MapperProvider;
import table.eat.now.coupon.coupon.application.utils.TimeProvider;
import table.eat.now.coupon.coupon.domain.command.CouponCachingAndIndexing;
import table.eat.now.coupon.coupon.domain.command.CouponIssuance;
import table.eat.now.coupon.coupon.domain.entity.Coupon;
import table.eat.now.coupon.coupon.domain.entity.CouponLabel;
import table.eat.now.coupon.coupon.infrastructure.exception.CouponInfraErrorCode;
import table.eat.now.coupon.coupon.infrastructure.persistence.redis.constant.CouponCacheConstant;
import table.eat.now.coupon.coupon.infrastructure.persistence.redis.constant.CouponLuaResultConstant.IssueResult;

@Slf4j
@RequiredArgsConstructor
@Repository
public class RedisCouponCacheManager {

  private static final String COUNT_PREFIX = "coupon:count:";

  private final RedisTemplate<String, Object> couponRedisTemplate;
  private final StringRedisTemplate stringRedisTemplate;

  public Coupon getCouponCache(String couponUuid) {
    String cacheKey = COUPON_CACHE + couponUuid;
    Map<Object, Object> couponMap = couponRedisTemplate.opsForHash().entries(cacheKey);
    if (couponMap.isEmpty()) {
      return null;
    }
    return MapperProvider.convertValue(couponMap, Coupon.class);
  }

  public void putCouponCache(String couponUuid, Coupon coupon, Duration ttl) {
    String cacheKey = COUPON_CACHE + couponUuid;

    couponRedisTemplate.opsForHash().putAll(
        cacheKey,
        MapperProvider.convertValue(coupon, new TypeReference<>() {}));
    couponRedisTemplate.expire(cacheKey, ttl);
  }

  public void putCouponCache(String couponUuid, Coupon coupon) {
    String cacheKey = COUPON_CACHE + couponUuid;

    couponRedisTemplate.opsForHash().putAll(
        cacheKey,
        MapperProvider.convertValue(coupon, new TypeReference<>() {}));
  }

  public Long increaseCouponCount(String key, String issuedCoupon) {
    return couponRedisTemplate.opsForHash().increment(key, issuedCoupon, 1);
  }

  public void pipelinedPutCouponsCacheAndIndex(List<CouponCachingAndIndexing> coupons) {
    List<Object> result = couponRedisTemplate.executePipelined(new SessionCallback<> () {
          @Override
          public Object execute(RedisOperations operations) {

            String hotIndexKey = DAILY_ISSUABLE_HOT_COUPON_INDEX + TimeProvider.getToday();
            String promoIndexKey = DAILY_ISSUABLE_PROMO_COUPON_INDEX + TimeProvider.getToday();

            for (CouponCachingAndIndexing coupon : coupons) {
              String cacheKey = COUPON_CACHE + coupon.couponUuid();
              operations.opsForHash().putAll(cacheKey, coupon.couponMap());
              operations.expire(cacheKey, coupon.ttl());
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

  public void pipelinedPutCouponsCache(List<CouponCachingAndIndexing> coupons) {
    List<Object> result = couponRedisTemplate.executePipelined(new SessionCallback<> () {
      @Override
      public Object execute(RedisOperations operations) {

        for (CouponCachingAndIndexing coupon : coupons) {
          String cacheKey = COUPON_CACHE + coupon.couponUuid();
          operations.opsForHash().putAll(cacheKey, coupon.couponMap());
          operations.expire(cacheKey, coupon.ttl());
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

    Set<Object> couponKeySet = couponRedisTemplate.opsForZSet().range(indexKey, 0, -1);
    if (couponKeySet == null || couponKeySet.isEmpty()) {
      return Collections.emptyList();
    }
    List<Object> couponKeys = couponKeySet.stream().toList();

    List<Object> results = couponRedisTemplate.executePipelined(new SessionCallback<> () {
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

    Long currentTimestamp = TimeProvider.getEpochMillis(LocalDateTime.now());

    List<String> keys = List.of(userSetKey, couponKey, idempotencyKey, DIRTY_COUPON_SET);
    List<String> args = List.of(command.userId().toString(), currentTimestamp.toString());

    Long result;
    try {
      result = executeLuaScript(
          LuaScriptType.LIMITED_NONDUP,
          keys,
          args);

    } catch (Exception e) {
      throw CustomException.from(CouponInfraErrorCode.FAILED_LUA_SCRIPT);
    }

    if (result != 1) {
      throw CustomException.from(IssueResult.parseToErrorCode(result));
    }
  }

  private Long executeLuaScript(
      LuaScriptType luaScriptType, List<String> keys, List<String> args) {

    RedisScript<Long> redisScript = (RedisScript<Long>) luaScriptType.getRedisScript();
    return stringRedisTemplate.execute(redisScript, keys, args.toArray(new String[0]));
  }

  public void setCouponCountWithTtl(String couponUuid, Integer value, Duration ttl) {
    couponRedisTemplate.opsForValue().set(COUNT_PREFIX + couponUuid, value, ttl);
  }

  public void setCouponSetWithTtl(String couponUuid, Duration ttl) {

    couponRedisTemplate.opsForSet().add(COUPON_USER_SET + couponUuid, "INIT");
    couponRedisTemplate.expire(COUPON_USER_SET + couponUuid, ttl);
  }

  public Long decreaseCouponCount(String couponUuid) {
    return couponRedisTemplate.opsForValue().decrement(COUNT_PREFIX + couponUuid);
  }

  public boolean isAlreadyIssued(String couponUuid, Long userId) {
    return Boolean.TRUE.equals(
        couponRedisTemplate.opsForSet().isMember(COUPON_USER_SET + couponUuid, userId));
  }

  public boolean markAsIssued(String couponUuid, Long userId) {
    Long addedCount = couponRedisTemplate.opsForSet().add(COUPON_USER_SET + couponUuid, userId);
    return addedCount != null && addedCount > 0;
  }

  public Long increaseCouponCount(String couponUuid) {
    return couponRedisTemplate.opsForValue().increment(COUNT_PREFIX + couponUuid);
  }

  public Integer getCouponCount(String couponUuid) {
    return (Integer) couponRedisTemplate.opsForValue().get(COUNT_PREFIX + couponUuid);
  }
}
