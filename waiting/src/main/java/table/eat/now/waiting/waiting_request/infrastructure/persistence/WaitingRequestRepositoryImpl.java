package table.eat.now.waiting.waiting_request.infrastructure.persistence;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import table.eat.now.waiting.waiting_request.domain.entity.WaitingRequest;
import table.eat.now.waiting.waiting_request.domain.repository.WaitingRequestRepository;
import table.eat.now.waiting.waiting_request.infrastructure.persistence.jpa.JpaWaitingRequestRepository;
import table.eat.now.waiting.waiting_request.infrastructure.persistence.redis.RedisWaitingRequestRepositoryImpl;

@RequiredArgsConstructor
@Repository
public class WaitingRequestRepositoryImpl implements WaitingRequestRepository {
  private final JpaWaitingRequestRepository jpaRepository;
  private final RedisWaitingRequestRepositoryImpl redisRepository;

  @Override
  public Long generateNextSequence(String dailyWaitingUuid) {
    return redisRepository.increaseSequence(dailyWaitingUuid);
  }

  @Override
  public boolean existsByConditionAndStatusIsWaitingAndDeletedAtIsNull(String dailyWaitingUuid, String phone) {
    return jpaRepository.existsByConditionAndStatusIsWaitingAndDeletedAtIsNull(
        dailyWaitingUuid, phone);
  }

  @Override
  public Boolean enqueueWaitingRequest(
      String dailyWaitingUuid, String waitingRequestUuid, long epochMilli) {
    return redisRepository.addWaitingRequest(dailyWaitingUuid, waitingRequestUuid, epochMilli);
  }

  @Override
  public WaitingRequest save(WaitingRequest waitingRequest) {
    return jpaRepository.save(waitingRequest);
  }

  @Override
  public Optional<WaitingRequest> findByWaitingRequestUuidAndDeletedAtIsNull(String waitingRequestUuid) {
    return jpaRepository.findByWaitingRequestUuidAndDeletedAtIsNull(waitingRequestUuid);
  }

  @Override
  public Integer getLastWaitingSequence(String dailyWaitingUuid) {
    return redisRepository.getSequence(dailyWaitingUuid);
  }

  @Override
  public Long getRank(String dailyWaitingUuid, String waitingRequestUuid) {
    return redisRepository.getRank(dailyWaitingUuid, waitingRequestUuid);
  }

  @Override
  public boolean dequeueWaitingRequest(String dailyWaitingUuid, String waitingRequestUuid) {
    return redisRepository.removeWaitingRequest(dailyWaitingUuid, waitingRequestUuid);
  }
}
