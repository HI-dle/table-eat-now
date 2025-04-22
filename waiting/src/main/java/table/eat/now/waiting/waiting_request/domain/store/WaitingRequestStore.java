package table.eat.now.waiting.waiting_request.domain.store;

import table.eat.now.waiting.waiting_request.domain.entity.WaitingRequest;

public interface WaitingRequestStore {

  Long generateNextSequence(String dailyWaitingUuid);

  Boolean enqueueWaitingRequest(String dailyWaitingUuid, String waitingRequestUuid);

  Boolean enqueueWaitingRequest(String dailyWaitingUuid, String waitingRequestUuid, long epochMilli);

  void dequeueWaitingRequest(String dailyWaitingUuid, String waitingRequestUuid);

  WaitingRequest save(WaitingRequest waitingRequest);
}
