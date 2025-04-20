package table.eat.now.waiting.waiting_request.domain.repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import table.eat.now.waiting.waiting_request.domain.criteria.CurrentWaitingRequestCriteria;
import table.eat.now.waiting.waiting_request.domain.entity.WaitingRequest;
import table.eat.now.waiting.waiting_request.domain.info.Paginated;

public interface WaitingRequestRepository {

  Long generateNextSequence(String dailyWaitingUuid);

  boolean existsByConditionAndStatusIsWaitingAndDeletedAtIsNull(String dailyWaitingUuid, String phone);

  Boolean enqueueWaitingRequest(
      String dailyWaitingUuid, String waitingRequestUuid, long epochMilli);

  WaitingRequest save(WaitingRequest waitingRequest);

  Optional<WaitingRequest> findByWaitingRequestUuidAndDeletedAtIsNull(String waitingRequestUuid);

  Integer getLastWaitingSequence(String dailyWaitingUuid);

  Long getRank(String dailyWaitingUuid, String waitingRequestUuid);

  boolean dequeueWaitingRequest(String s, String waitingRequestsUuid);

  Set<String> getIdsInRange(String dailyWaitingUuid, long start, long end);

  Paginated<WaitingRequest> getCurrentWaitingRequests(CurrentWaitingRequestCriteria criteria);

  List<WaitingRequest> findByWaitingRequestUuidInAndDeletedAtIsNull(Set<String> idsSet);

  Long countCurrentWaitingRequests(String dailyWaitingUuid);
}
