package table.eat.now.promotion.promotion.infrastructure.redis.script;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import table.eat.now.common.exception.CustomException;
import table.eat.now.promotion.promotion.application.exception.PromotionErrorCode;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 25.
 */
@Slf4j
public class AddScheduleQueueToPromotionCommand implements RedisScriptCommand<List<String>> {

  private final RedisTemplate<String, String> redisTemplate;
  private final DefaultRedisScript<List> script;
  private final String key;

  private static final double BUFFER_TIME = 30.00;

  public AddScheduleQueueToPromotionCommand(
      RedisTemplate<String, String> redisTemplate,
      PromotionLuaScriptProvider scriptProvider,
      String key
  ) {
    this.redisTemplate = redisTemplate;
    this.script = new DefaultRedisScript<>();
    this.script.setScriptText(scriptProvider.getPollScheduleQueueScript());
    this.script.setResultType(List.class);
    this.key = key;
  }
  @Override
  public List<String> execute() {
    try {
      log.info("실행은 함");
      double now = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
      List<String> result = redisTemplate.execute(
          script,
          Collections.singletonList(key),
          String.valueOf(now + BUFFER_TIME)
      );

      return java.util.Objects.requireNonNullElseGet(result, Collections::emptyList);
    } catch (Exception e) {
      log.error("pollScheduleQueue 실패: {}", e.getMessage(), e);
      throw CustomException.from(PromotionErrorCode.PROMOTION_LUA_SCRIPT_FAILED);
    }
  }
}
