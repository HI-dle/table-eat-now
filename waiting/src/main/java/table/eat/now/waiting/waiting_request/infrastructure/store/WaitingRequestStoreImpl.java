package table.eat.now.waiting.waiting_request.infrastructure.store;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import table.eat.now.common.exception.CustomException;
import table.eat.now.waiting.waiting_request.domain.entity.WaitingRequest;
import table.eat.now.waiting.waiting_request.domain.store.WaitingRequestStore;
import table.eat.now.waiting.waiting_request.infrastructure.exception.WaitingRequestInfraErrorCode;
import table.eat.now.waiting.waiting_request.infrastructure.persistence.jpa.JpaWaitingRequestRepository;
import table.eat.now.waiting.waiting_request.infrastructure.persistence.redis.RedisWaitingRequestRepositoryImpl;
import table.eat.now.waiting.waiting_request.infrastructure.store.utils.TimeProvider;

@RequiredArgsConstructor
@Repository
public class WaitingRequestStoreImpl implements WaitingRequestStore {

  private final JpaWaitingRequestRepository jpaRepository;
  private final RedisWaitingRequestRepositoryImpl redisRepository;

  @Override
  public Long generateNextSequence(String dailyWaitingUuid) {
    return redisRepository.increaseSequence(dailyWaitingUuid);
  }

  @Override
  public Boolean enqueueWaitingRequest(String dailyWaitingUuid, String waitingRequestUuid, long epochMilli) {

    Boolean result = redisRepository.addWaitingRequest(dailyWaitingUuid, waitingRequestUuid, epochMilli);
    if (result == null) {
      throw CustomException.from(WaitingRequestInfraErrorCode.FAILED_ENQUEUE);
    }
    return result;
  }

  @Override
  public Boolean enqueueWaitingRequest(String dailyWaitingUuid, String waitingRequestUuid) {
    long epochMilli = TimeProvider.currentTimeMillis();
    Boolean result = redisRepository.addWaitingRequest(dailyWaitingUuid, waitingRequestUuid, epochMilli);
    if (result == null) {
      throw CustomException.from(WaitingRequestInfraErrorCode.FAILED_ENQUEUE);
    }
    return result;
  }

  @Override
  public void dequeueWaitingRequest(String dailyWaitingUuid, String waitingRequestUuid) {
    boolean result =
        redisRepository.removeWaitingRequest(dailyWaitingUuid, waitingRequestUuid);
    if (!result) {
      throw CustomException.from(WaitingRequestInfraErrorCode.INVALID_WAITING_REQUEST_UUID);
    }
  }

  @Override
  public WaitingRequest save(WaitingRequest waitingRequest) {
    return jpaRepository.save(waitingRequest);
  }
}
