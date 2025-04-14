package table.eat.now.waiting.waiting_request.domain.repository;

import java.util.Optional;
import table.eat.now.waiting.waiting_request.domain.entity.WaitingRequest;

public interface WaitingRequestRepository {

  Long generateNextSequence(String dailyWaitingUuid);

  boolean existsByConditionAndStatusIsWaitingAndDeletedAtIsNull(String dailyWaitingUuid, String phone);

  boolean enqueueWaitingRequest(
      String dailyWaitingUuid, String waitingRequestUuid, long epochMilli);

  WaitingRequest save(WaitingRequest waitingRequest);

  Optional<WaitingRequest> findByWaitingRequestUuidAndDeletedAtIsNull(String waitingRequestUuid);

  Integer getLastWaitingSequence(String dailyWaitingUuid);

  Long getRank(String dailyWaitingUuid, String waitingRequestUuid);

  boolean dequeueWaitingRequest(String s, String waitingRequestsUuid);
}
