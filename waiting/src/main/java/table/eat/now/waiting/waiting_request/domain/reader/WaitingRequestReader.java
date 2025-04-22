package table.eat.now.waiting.waiting_request.domain.reader;

import java.util.List;
import java.util.Set;
import table.eat.now.waiting.waiting_request.domain.criteria.CurrentWaitingRequestCriteria;
import table.eat.now.waiting.waiting_request.domain.entity.WaitingRequest;
import table.eat.now.waiting.waiting_request.domain.info.Paginated;

public interface WaitingRequestReader {

  Integer getLastWaitingSequence(String dailyWaitingUuid);

  Long getRank(String dailyWaitingUuid, String waitingRequestUuid);

  Set<String> getIdsInRange(String dailyWaitingUuid, long start, long end);

  Paginated<WaitingRequest> getCurrentWaitingRequests(
      CurrentWaitingRequestCriteria criteria);

  Long getRankIfWaiting(String waitingRequestUuid, WaitingRequest waitingRequest);

  List<WaitingRequest> findByWaitingRequestUuidInAndDeletedAtIsNull(Set<String> idsSet);

  Long countCurrentWaitingRequests(String dailyWaitingUuid);

  WaitingRequest getWaitingRequestBy(String waitingRequestUuid);

  boolean existsDuplicateWaiting(String dailyWaitingUuid, String phone);
}
