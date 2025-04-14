package table.eat.now.waiting.waiting_request.infrastructure.persistence.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class RedisWaitingRequestRepositoryImpl implements RedisWaitingRequestRepository {
  private static final String SEQUENCE_PREFIX = "waiting:count:";
  private static final String WAITING_ZSET_PREFIX = "waiting:zset:";
  private final RedisTemplate<String, Object> redisTemplate;

  @Override
  public Long increaseSequence(String dailyWaitingUuid) {
    return redisTemplate.opsForValue().increment(SEQUENCE_PREFIX + dailyWaitingUuid);
  }

  @Override
  public boolean addWaitingRequest(
      String dailyWaitingUuid, String waitingRequestUuid, long timestamp) {
    return Boolean.TRUE.equals(
        redisTemplate.opsForZSet()
            .add(WAITING_ZSET_PREFIX + dailyWaitingUuid, waitingRequestUuid, timestamp));
  }

  @Override
  public Integer getSequence(String dailyWaitingUuid) {
    return (Integer) redisTemplate.opsForValue().get(SEQUENCE_PREFIX + dailyWaitingUuid);
  }

  @Override
  public Long getRank(String dailyWaitingUuid, String waitingRequestUuid) {
    return redisTemplate.opsForZSet().rank(WAITING_ZSET_PREFIX + dailyWaitingUuid, waitingRequestUuid);
  }
}
