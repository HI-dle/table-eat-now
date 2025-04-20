package table.eat.now.waiting.waiting_request.infrastructure.persistence.redis;

import java.util.Set;

public interface RedisWaitingRequestRepository {

  Long increaseSequence(String dailyWaitingUuid);

  Boolean addWaitingRequest(String dailyWaitingUuid, String waitingRequestUuid, long timestamp);

  Integer getSequence(String dailyWaitingUuid);

  Long getRank(String dailyWaitingUuid, String waitingRequestUuid);

  boolean removeWaitingRequest(String dailyWaitingUuid, String waitingRequestUuid);

  Set<String> getIdsInRange(String dailyWaitingUuid, long start, long end);

  Long countCurrentWaitingRequests(String dailyWaitingUuid);
}
