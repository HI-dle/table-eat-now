package table.eat.now.waiting.waiting_request.infrastructure.reader;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import table.eat.now.common.exception.CustomException;
import table.eat.now.waiting.waiting_request.domain.criteria.CurrentWaitingRequestCriteria;
import table.eat.now.waiting.waiting_request.domain.entity.WaitingRequest;
import table.eat.now.waiting.waiting_request.domain.info.Paginated;
import table.eat.now.waiting.waiting_request.domain.reader.WaitingRequestReader;
import table.eat.now.waiting.waiting_request.infrastructure.exception.WaitingRequestInfraErrorCode;
import table.eat.now.waiting.waiting_request.infrastructure.persistence.jpa.JpaWaitingRequestRepository;
import table.eat.now.waiting.waiting_request.infrastructure.persistence.redis.RedisWaitingRequestRepositoryImpl;

@RequiredArgsConstructor
@Repository
public class WaitingRequestReaderImpl implements WaitingRequestReader {
  private final JpaWaitingRequestRepository jpaRepository;
  private final RedisWaitingRequestRepositoryImpl redisRepository;

  @Override
  public Integer getLastWaitingSequence(String dailyWaitingUuid) {
    return redisRepository.getSequence(dailyWaitingUuid);
  }

  @Override
  public Long countCurrentWaitingRequests(String dailyWaitingUuid) {
    return redisRepository.countCurrentWaitingRequests(dailyWaitingUuid);
  }

  @Override
  public Long getRank(String dailyWaitingUuid, String waitingRequestUuid) {
    return redisRepository.getRank(dailyWaitingUuid, waitingRequestUuid);
  }

  @Override
  public Long getRankIfWaiting(String waitingRequestUuid, WaitingRequest waitingRequest) {
    if (!waitingRequest.isWaiting()) {
      return null;
    }
    return redisRepository.getRank(waitingRequest.getDailyWaitingUuid(), waitingRequestUuid);
  }

  @Override
  public Set<String> getIdsInRange(String dailyWaitingUuid, long start, long end) {
    return redisRepository.getIdsInRange(dailyWaitingUuid, start, end);
  }

  @Override
  public WaitingRequest getWaitingRequestBy(String waitingRequestUuid) {
    return jpaRepository.findByWaitingRequestUuidAndDeletedAtIsNull(waitingRequestUuid)
        .orElseThrow(
            () -> CustomException.from(WaitingRequestInfraErrorCode.INVALID_WAITING_REQUEST_UUID));
  }

  @Override
  public boolean existsDuplicateWaiting(String dailyWaitingUuid, String phone) {
    return jpaRepository.existsByConditionAndStatusIsWaitingAndDeletedAtIsNull(dailyWaitingUuid, phone);
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
    if (!requests.isEmpty() && requests.size() < size) {
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
}