package table.eat.now.coupon.coupon.infrastructure.persistence.redis;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Set;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.scripting.support.ResourceScriptSource;
import org.springframework.util.StreamUtils;
import table.eat.now.common.exception.CustomException;
import table.eat.now.coupon.coupon.domain.info.CouponProfile;
import table.eat.now.coupon.coupon.infrastructure.exception.CouponInfraErrorCode;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum LuaScriptType {
  LIMITED_NONDUP(
      Set.of(CouponProfile.PROMO_LIMITED_NONDUP, CouponProfile.HOT_LIMITED_NONDUP),
      getScript("redis/coupon/limited_nondup.lua", Long.class),
      "redis/coupon/limited_nondup.lua"
  ),
  ;

  private final Set<CouponProfile> profiles;
  private final RedisScript<?> redisScript;
  private final String scriptFilePath;
  private String luaSha;

  // 루아스크립트가 늘어나면 해시맵에 캐싱해두는 걸로 리팩토링할 필요성이 존재함
  public LuaScriptType from(CouponProfile profile) {
    return Arrays.stream(LuaScriptType.values())
        .filter(type -> type.getProfiles().contains(profile))
        .findFirst()
        .orElseThrow(() -> CustomException.from(CouponInfraErrorCode.NOT_FOUND_LUA_SCRIPT));
  }

  // 루아스크립트를 레디스에 로드하는 경우 활용가능
  public String getLusSha(StringRedisTemplate redisTemplate) {
    if (this.luaSha == null) {
      luaSha = loadScriptAndGetSha(redisTemplate);
    }
    return this.luaSha;
  }

  private static <T> RedisScript<T> getScript(String path, Class<T> resultType) {
    DefaultRedisScript<T> script = new DefaultRedisScript<>();
    script.setScriptSource(new ResourceScriptSource(new ClassPathResource(path)));
    script.setResultType(resultType);
    return script;
  }

  private String loadScriptAndGetSha(StringRedisTemplate redisTemplate) {

    String sha = redisTemplate.opsForValue().get(scriptFilePath);

    if (sha == null) {
      String luaScript = getScriptString();
      sha = redisTemplate.execute((RedisCallback<String>) connection ->
          connection.scriptingCommands().scriptLoad(luaScript.getBytes(StandardCharsets.UTF_8))
      );
      redisTemplate.opsForValue().set(scriptFilePath, sha);
    }
    return sha;
  }

  private String getScriptString() {

    Resource resource = new ClassPathResource(scriptFilePath);
    try {
      return StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);
    } catch (IOException e) {
      throw CustomException.from(CouponInfraErrorCode.NOT_FOUND_LUA_SCRIPT);
    }
  }
}
