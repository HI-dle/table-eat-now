package table.eat.now.promotion.promotion.infrastructure.redis;

import java.time.ZoneOffset;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;
import table.eat.now.promotion.promotion.domain.entity.Promotion;
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
  private static final String SCHEDULE_VALUE_START_SUFFIX = ":start";
  private static final String SCHEDULE_VALUE_END_SUFFIX = ":end";

  @Value("${schedule-key}")
  String scheduleKey;

  @Override
  public ParticipateResult addUserToPromotion(PromotionParticipant participant, int maxCount) {
    PromotionUserCommand command = PromotionUserCommand.from(participant);

    String key = buildKey(command.promotionName());

    RedisScriptCommand scriptCommand = redisScriptCommandFactory
        .createAddUserCommand(command, key, maxCount);

    Long result = (Long) scriptCommand.execute();

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

  //스케줄러 실행 종료 자동화를 위한 레디스 큐에 삽입하는 메서드

  @Override
  public void addScheduleQueue(Promotion promotion) {

    saveRedisScheduleQueueToStart(promotion);
    saveRedisScheduleQueueToEnd(promotion);
  }

  //스케줄러 실행 종료 자동화를 위한 레디스 큐에서 꺼내오는 메서드
  @Override
  public List<String> pollScheduleQueue() {
    RedisScriptCommand<List<String>> command =
        redisScriptCommandFactory.addScheduleQueueCommand(buildScheduleKey());

    return command.execute();
  }

  private PromotionUserSavePayloadQuery parseToPayload(String query) {
    String[] parts = query.split(":");
    return new PromotionUserSavePayloadQuery(Long.valueOf(parts[0]), parts[1]);
  }


  private String buildKey(String promotionName) {
    return PROMOTION_KEY_PREFIX + promotionName;
  }

  private String buildScheduleKey() {
    return PROMOTION_KEY_PREFIX + scheduleKey;
  }

  private void saveRedisScheduleQueueToStart(Promotion promotion) {
    double score = promotion.getPeriod().getStartTime().toEpochSecond(ZoneOffset.UTC);
    redisTemplate.opsForZSet().add(
        buildScheduleKey(), promotion.getPromotionUuid() + SCHEDULE_VALUE_START_SUFFIX, score);
  }

  private void saveRedisScheduleQueueToEnd(Promotion promotion) {
    double score = promotion.getPeriod().getEndTime().toEpochSecond(ZoneOffset.UTC);
    redisTemplate.opsForZSet().add(
        buildScheduleKey(), promotion.getPromotionUuid() + SCHEDULE_VALUE_END_SUFFIX, score);
  }


}

