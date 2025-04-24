package table.eat.now.promotion.promotion.infrastructure.redis.script;

import io.micrometer.core.instrument.MeterRegistry;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import table.eat.now.promotion.promotion.infrastructure.dto.request.PromotionUserCommand;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 19.
 */
@Component
@RequiredArgsConstructor
public class RedisScriptCommandFactory {

  private final RedisTemplate<String, String> redisTemplate;
  private final PromotionLuaScriptProvider scriptProvider;
  private final MeterRegistry meterRegistry;

  public RedisScriptCommand<Long> createAddUserCommand(PromotionUserCommand command, String key, int maxCount) {
    return new AddUserToPromotionCommand(redisTemplate, scriptProvider,meterRegistry, command, key, maxCount);
  }
  public RedisScriptCommand<List<String>> addScheduleQueueCommand(String key) {
    return new AddScheduleQueueToPromotionCommand(redisTemplate, scriptProvider, key);
  }
}

