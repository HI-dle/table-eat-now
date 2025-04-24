package table.eat.now.notification.infrastructure.redis;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Repository;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 23.
 */
@Repository
@RequiredArgsConstructor
@Slf4j
public class NotificationRedisRepositoryImpl implements NotificationRedisRepository{

  private final RedisTemplate<String, String> redisTemplate;

  private final DefaultRedisScript<List> popDueScript;

  @Value("${notification.delay-queue-key}")
  private String delayQueue;


  @Override
  public void addToDelayQueue(String notificationUuId, LocalDateTime scheduledTime) {
    long score = scheduledTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    redisTemplate.opsForZSet().add(delayQueue, notificationUuId, score);
  }


  @Override
  public List<String> popDueNotifications(int maxCount) {
    long now = System.currentTimeMillis();
    // 스크립트를 실행하고 결과를 String 리스트로 받음
    List<String> result = redisTemplate.execute(
        popDueScript,
        List.of(delayQueue),
        String.valueOf(now), String.valueOf(maxCount)
    );

    return validResult(result);
  }

  private static List<String> validResult(List<String> result) {
    // 결과가 null 이거나 빈 리스트일 경우, 빈 리스트를 반환
    if (result == null || result.isEmpty()) {
      log.debug("처리한 알림이 없거나 스크립트에 문제가 생겼습니다.");
      return List.of();
    }

    return result;
  }

}
