package table.eat.now.coupon.coupon.infrastructure.persistence.redis;

import java.util.Arrays;
import java.util.Set;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.scripting.support.ResourceScriptSource;
import table.eat.now.common.exception.CustomException;
import table.eat.now.coupon.coupon.domain.info.CouponProfile;
import table.eat.now.coupon.coupon.infrastructure.exception.CouponInfraErrorCode;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum LuaScriptType {
  LIMITED_NONDUP(
      Set.of(CouponProfile.PROMO_LIMITED_NONDUP, CouponProfile.HOT_LIMITED_NONDUP),
      loadScript("redis/coupon/limited_nondup.lua", Long.class))
  ;

  private final Set<CouponProfile> profiles;
  private final RedisScript<?> redisScript;

  private static <T> RedisScript<T> loadScript(String path, Class<T> resultType) {
    DefaultRedisScript<T> script = new DefaultRedisScript<>();
    script.setScriptSource(new ResourceScriptSource(new ClassPathResource(path)));
    script.setResultType(resultType);
    return script;
  }

  public static LuaScriptType from(CouponProfile profile) {
    return Arrays.stream(LuaScriptType.values())
        .filter(type -> type.getProfiles().contains(profile))
        .findFirst()
        .orElseThrow(() -> CustomException.from(CouponInfraErrorCode.NOT_FOUND_LUA_SCRIPT));
  }
}
