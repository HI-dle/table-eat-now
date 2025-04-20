package table.eat.now.waiting.waiting_request.infrastructure.persistence.redis;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
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
  public Boolean addWaitingRequest(
      String dailyWaitingUuid, String waitingRequestUuid, long timestamp) {
    return redisTemplate.opsForZSet()
            .add(WAITING_ZSET_PREFIX + dailyWaitingUuid, waitingRequestUuid, timestamp);
  }

  @Override
  public Integer getSequence(String dailyWaitingUuid) {
    return (Integer) redisTemplate.opsForValue().get(SEQUENCE_PREFIX + dailyWaitingUuid);
  }

  @Override
  public Long getRank(String dailyWaitingUuid, String waitingRequestUuid) {
    return redisTemplate.opsForZSet().rank(WAITING_ZSET_PREFIX + dailyWaitingUuid, waitingRequestUuid);
  }

  @Override
  public boolean removeWaitingRequest(String dailyWaitingUuid, String waitingRequestUuid) {
    Long count = redisTemplate.opsForZSet().remove(WAITING_ZSET_PREFIX + dailyWaitingUuid, waitingRequestUuid);
    return count != null && count > 0;
  }

  @Override
  public Set<String> getIdsInRange(String dailyWaitingUuid, long start, long end) {

    return Optional.ofNullable(redisTemplate
            .opsForZSet()
            .range(WAITING_ZSET_PREFIX + dailyWaitingUuid, start, end))
        .orElse(Collections.emptySet())
        .stream()
        .filter(o -> o instanceof String)
        .map(o -> (String) o)
        .collect(Collectors.toCollection(LinkedHashSet::new));
  }

  @Override
  public Long countCurrentWaitingRequests(String dailyWaitingUuid) {
    return redisTemplate.opsForZSet().zCard(WAITING_ZSET_PREFIX + dailyWaitingUuid);
  }
}
