package table.eat.now.promotion.promotion.infrastructure.redis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Collections;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Repository;
import table.eat.now.promotion.promotion.domain.entity.repository.event.PromotionParticipant;
import table.eat.now.promotion.promotion.infrastructure.dto.request.PromotionUserCommand;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 15.
 */
@Repository
@RequiredArgsConstructor
@Slf4j
public class PromotionRedisRepositoryImpl implements PromotionRedisRepository {


  private final RedisTemplate<String, String> redisTemplate;
  private final ObjectMapper objectMapper;
  private final PromotionLuaScriptProvider luaScriptProvider;

  private static final String PROMOTION_KEY_PREFIX = "promotion:";

  @Override
  public boolean addUserToPromotion(PromotionParticipant participant, int maxCount) {
    PromotionUserCommand command = PromotionUserCommand.from(participant);

    String key = buildKey(command.promotionName());
    long now = System.currentTimeMillis();

    DefaultRedisScript<Long> script = new DefaultRedisScript<>();
    script.setScriptText(luaScriptProvider.getAddUserScript());
    script.setResultType(Long.class);

    try {
      Long result = redisTemplate.execute(
          script,
          Collections.singletonList(key),
          String.valueOf(maxCount),
          String.valueOf(now),
          String.valueOf(command.userId()),
          command.promotionUuid()

      );
      log.info("Lua Script Key: {}", key);
      log.info("Max Count: {}", maxCount);
      log.info("Now: {}", now);
      log.info("Serialized Command: {}", serialize(command));

      return result != null && result == 1L;
    } catch (Exception e) {
      log.error("Redis Lua Script Error", e);
      throw e;
    }
  }

  private String buildKey(String promotionName) {
    return PROMOTION_KEY_PREFIX + promotionName;
  }

  private String serialize(PromotionUserCommand command) {
    try {
      return objectMapper.writeValueAsString(command);
    } catch (JsonProcessingException e) {
      throw new RuntimeException("Failed to serialize PromotionUserQuery", e);
    }
  }
}
