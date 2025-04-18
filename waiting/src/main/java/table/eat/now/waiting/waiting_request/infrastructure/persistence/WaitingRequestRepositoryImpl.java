package table.eat.now.waiting.waiting_request.infrastructure.persistence;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import table.eat.now.waiting.waiting_request.domain.criteria.CurrentWaitingRequestCriteria;
import table.eat.now.waiting.waiting_request.domain.entity.WaitingRequest;
import table.eat.now.waiting.waiting_request.domain.info.Paginated;
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

  @Override
  public Set<String> getIdsInRange(String dailyWaitingUuid, long start, long end) {
    return redisRepository.getIdsInRange(dailyWaitingUuid, start, end);
  }

  @Override
  public Paginated<WaitingRequest> getCurrentWaitingRequests(
      CurrentWaitingRequestCriteria criteria) {

    int size = criteria.size();
    int start =  criteria.page() * size;
    long end = start + size - 1;

    Set<String> idsSet = redisRepository.getIdsInRange(criteria.dailyWaitingUuid(), start, end);
    Map<String ,WaitingRequest> requestsMap =
        jpaRepository.findByWaitingRequestUuidInAndDeletedAtIsNull(idsSet)
            .stream()
            .collect(Collectors.toMap(
                WaitingRequest::getWaitingRequestUuid,
                Function.identity()
            ));

    List<WaitingRequest> requests = idsSet.stream()
        .map(requestsMap::get)
        .toList();

    long total = 0;
    if (requests.size() < size) {
      total = start + requests.size();
    } else {
      total = redisRepository.countCurrentWaitingRequests(criteria.dailyWaitingUuid());
    }
    int totalPages = (int) ((total + size - 1) / size);
    return Paginated.of(requests, total, totalPages, criteria.page(), size);
  }

  @Override
  public List<WaitingRequest> findByWaitingRequestUuidInAndDeletedAtIsNull(Set<String> idsSet) {
    return jpaRepository.findByWaitingRequestUuidInAndDeletedAtIsNull(idsSet);
  }

  @Override
  public Long countCurrentWaitingRequests(String dailyWaitingUuid) {
    return redisRepository.countCurrentWaitingRequests(dailyWaitingUuid);
  }
}
