package table.eat.now.notification.infrastructure.redis;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
  private static final String DELAY_QUEUE_KEY = "notification:delay-queue";

  private static final DefaultRedisScript<List> POP_DUE_SCRIPT = new DefaultRedisScript<>();

  static {
    POP_DUE_SCRIPT.setScriptText(
        "local results = redis.call('ZRANGEBYSCORE', KEYS[1], 0, ARGV[1], 'LIMIT', 0, ARGV[2]) " +
            "for i, v in ipairs(results) do redis.call('ZREM', KEYS[1], v) end " +
            "return results"
    );
    POP_DUE_SCRIPT.setResultType(List.class);
  }
  @Override
  public void addToDelayQueue(Long notificationId, LocalDateTime scheduledTime) {
    long score = scheduledTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    redisTemplate.opsForZSet().add(DELAY_QUEUE_KEY, String.valueOf(notificationId), score);
  }

  @Override
  public List<Long> popDueNotifications(int maxCount) {
    long now = System.currentTimeMillis();
    List<String> result = redisTemplate.execute(
        POP_DUE_SCRIPT,
        List.of(DELAY_QUEUE_KEY),
        String.valueOf(now), String.valueOf(maxCount)
    );

    if (result == null) return List.of();
    return result.stream().map(Long::valueOf).toList();
  }
}
