package table.eat.now.promotion.promotion.infrastructure.redis;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;
import table.eat.now.promotion.promotion.domain.entity.repository.event.ParticipateResult;
import table.eat.now.promotion.promotion.domain.entity.repository.event.PromotionParticipant;
import table.eat.now.promotion.promotion.domain.entity.repository.event.PromotionParticipantDto;
import table.eat.now.promotion.promotion.infrastructure.dto.request.PromotionUserCommand;
import table.eat.now.promotion.promotion.infrastructure.kafka.dto.PromotionUserSavePayloadQuery;
import table.eat.now.promotion.promotion.infrastructure.redis.script.RedisScriptCommand;
import table.eat.now.promotion.promotion.infrastructure.redis.script.RedisScriptCommandFactory;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 15.
 */
@Repository
@RequiredArgsConstructor
@Slf4j
public class PromotionRedisRepositoryImpl implements PromotionRedisRepository {


  private final RedisTemplate<String, String> redisTemplate;
  private final RedisScriptCommandFactory redisScriptCommandFactory;

  private static final String PROMOTION_KEY_PREFIX = "promotion:";

  @Override
  public ParticipateResult addUserToPromotion(PromotionParticipant participant, int maxCount) {
    PromotionUserCommand command = PromotionUserCommand.from(participant);

    String key = buildKey(command.promotionName());

    RedisScriptCommand scriptCommand = redisScriptCommandFactory
        .createAddUserCommand(command, key, maxCount);

    Long result = scriptCommand.execute();

    return ParticipateResult.from(result);
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

}

