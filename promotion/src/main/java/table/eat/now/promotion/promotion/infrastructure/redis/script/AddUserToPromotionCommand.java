package table.eat.now.promotion.promotion.infrastructure.redis.script;

import java.util.Collections;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import table.eat.now.promotion.promotion.domain.entity.repository.event.PromotionParticipant;
import table.eat.now.promotion.promotion.infrastructure.dto.request.PromotionUserCommand;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 19.
 */
public class AddUserToPromotionCommand implements RedisScriptCommand {

  private final RedisTemplate<String, String> redisTemplate;
  private final DefaultRedisScript<Long> script;
  private final String key;
  private final int maxCount;
  private final String userId;
  private final String promotionUuid;

  public AddUserToPromotionCommand(
      RedisTemplate<String, String> redisTemplate,
      PromotionLuaScriptProvider scriptProvider,
      PromotionParticipant participant,
      String key,
      int maxCount) {
    this.redisTemplate = redisTemplate;
    this.script = new DefaultRedisScript<>();
    this.script.setScriptText(scriptProvider.getAddUserScript());
    this.script.setResultType(Long.class);

    PromotionUserCommand command = PromotionUserCommand.from(participant);
    this.key = key;
    this.maxCount = maxCount;
    this.userId = String.valueOf(command.userId());
    this.promotionUuid = command.promotionUuid();
  }

  @Override
  public Long execute() {
    long now = System.currentTimeMillis();
    return redisTemplate.execute(
        script,
        Collections.singletonList(key),
        String.valueOf(maxCount),
        String.valueOf(now),
        userId,
        promotionUuid
    );
  }
}

