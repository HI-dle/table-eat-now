package table.eat.now.promotion.promotion.infrastructure.redis.script;

import static table.eat.now.promotion.promotion.infrastructure.metric.PromotionMetricName.PROMOTION_PARTICIPATION_FAIL;
import static table.eat.now.promotion.promotion.infrastructure.metric.PromotionMetricName.PROMOTION_PARTICIPATION_LATENCY;
import static table.eat.now.promotion.promotion.infrastructure.metric.PromotionMetricName.PROMOTION_PARTICIPATION_SUCCESS;

import io.micrometer.core.instrument.MeterRegistry;
import java.util.Collections;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import table.eat.now.common.exception.CustomException;
import table.eat.now.promotion.promotion.application.exception.PromotionErrorCode;
import table.eat.now.promotion.promotion.infrastructure.dto.request.PromotionUserCommand;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 19.
 */
@Slf4j
public class AddUserToPromotionCommand implements RedisScriptCommand {

  private final MeterRegistry meterRegistry;

  private final RedisTemplate<String, String> redisTemplate;
  private final DefaultRedisScript<Long> script;
  private final String key;
  private final int maxCount;
  private final String userId;
  private final String promotionUuid;

  public AddUserToPromotionCommand(
      RedisTemplate<String, String> redisTemplate,
      PromotionLuaScriptProvider scriptProvider, MeterRegistry meterRegistry,
      PromotionUserCommand command,
      String key,
      int maxCount) {
    this.redisTemplate = redisTemplate;
    this.meterRegistry = meterRegistry;
    this.script = new DefaultRedisScript<>();
    this.script.setScriptText(scriptProvider.getAddUserScript());
    this.script.setResultType(Long.class);

    this.key = key;
    this.maxCount = maxCount;
    this.userId = String.valueOf(command.userId());
    this.promotionUuid = command.promotionUuid();
  }

  @Override
  public Long execute() {
    long now = System.currentTimeMillis();
    try {
      Long result = meterRegistry.timer(PROMOTION_PARTICIPATION_LATENCY)
          .record(() -> redisTemplate.execute(
              script,
              Collections.singletonList(key),
              String.valueOf(maxCount),
              String.valueOf(now),
              userId,
              promotionUuid
          ));

      meterRegistry.counter(PROMOTION_PARTICIPATION_SUCCESS).increment();

      return result;

    } catch (Exception e) {
      meterRegistry.counter(PROMOTION_PARTICIPATION_FAIL).increment();
      log.error("Redis 스크립트 실행 중 오류 발생: {}", e.getMessage(), e);
      throw CustomException.from(PromotionErrorCode.PROMOTION_LUA_SCRIPT_FAILED);
    }
  }
}

