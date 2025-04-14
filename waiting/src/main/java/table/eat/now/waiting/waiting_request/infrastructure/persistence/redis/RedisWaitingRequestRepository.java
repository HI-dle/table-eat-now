package table.eat.now.waiting.waiting_request.infrastructure.persistence.redis;

public interface RedisWaitingRequestRepository {

  Long increaseSequence(String dailyWaitingUuid);

  boolean addWaitingRequest(String dailyWaitingUuid, String waitingRequestUuid, long timestamp);

  Integer getSequence(String dailyWaitingUuid);

  Long getRank(String dailyWaitingUuid, String waitingRequestUuid);

  boolean removeWaitingRequest(String dailyWaitingUuid, String waitingRequestUuid);
}
