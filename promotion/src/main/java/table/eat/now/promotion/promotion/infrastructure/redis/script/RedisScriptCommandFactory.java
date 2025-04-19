package table.eat.now.promotion.promotion.infrastructure.redis.script;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import table.eat.now.promotion.promotion.domain.entity.repository.event.PromotionParticipant;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 19.
 */
@Component
@RequiredArgsConstructor
public class RedisScriptCommandFactory {

  private final RedisTemplate<String, String> redisTemplate;
  private final PromotionLuaScriptProvider scriptProvider;

  public RedisScriptCommand createAddUserCommand(PromotionParticipant participant, String key, int maxCount) {
    return new AddUserToPromotionCommand(redisTemplate, scriptProvider, participant, key, maxCount);
  }
}

