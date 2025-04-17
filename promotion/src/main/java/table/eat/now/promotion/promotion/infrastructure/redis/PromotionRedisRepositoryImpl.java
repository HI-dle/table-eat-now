package table.eat.now.promotion.promotion.infrastructure.redis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Repository;
import table.eat.now.common.exception.CustomException;
import table.eat.now.promotion.promotion.application.exception.PromotionErrorCode;
import table.eat.now.promotion.promotion.domain.entity.repository.event.ParticipateResult;
import table.eat.now.promotion.promotion.domain.entity.repository.event.PromotionParticipant;
import table.eat.now.promotion.promotion.domain.entity.repository.event.PromotionParticipantDto;
import table.eat.now.promotion.promotion.infrastructure.dto.request.PromotionUserCommand;
import table.eat.now.promotion.promotion.infrastructure.kafka.dto.PromotionUserSavePayloadQuery;

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
  public ParticipateResult addUserToPromotion(PromotionParticipant participant, int maxCount) {
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
      if (result == null || result == 0L) {
        return ParticipateResult.FAIL;
      }
      if (result == 2L) {
        return ParticipateResult.SUCCESS_SEND_BATCH;
      }
      return ParticipateResult.SUCCESS;
    } catch (Exception e) {
      log.error("Redis Lua Script Error", e);
      throw CustomException.from(PromotionErrorCode.PROMOTION_LUA_SCRIPT_FAILED);
    }
  }


  @Override
  public List<PromotionParticipantDto> getPromotionUsers(String promotionName) {
    String key = buildKey(promotionName);
    Set<String> queryData = redisTemplate.opsForZSet().range(key, 0, 999);
    if (queryData == null || queryData.isEmpty()) return Collections.emptyList();

    return queryData.stream()
        .map(this::parseToPayload)
        .map(PromotionUserSavePayloadQuery::from)
        .toList();
  }

  private PromotionUserSavePayloadQuery parseToPayload(String query) {
    String[] parts = query.split(":");
    return new PromotionUserSavePayloadQuery(Long.valueOf(parts[0]), parts[1]);
  }


  private String buildKey(String promotionName) {
    return PROMOTION_KEY_PREFIX + promotionName;
  }

  private String serialize(PromotionUserCommand command) {
    try {
      return objectMapper.writeValueAsString(command);
    } catch (JsonProcessingException e) {
      throw CustomException.from(PromotionErrorCode.PROMOTION_SERIALIZATION_FAILED);
    }
  }
}
