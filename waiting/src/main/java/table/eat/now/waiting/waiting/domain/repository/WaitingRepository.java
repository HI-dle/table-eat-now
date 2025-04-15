package table.eat.now.waiting.waiting.domain.repository;

import table.eat.now.waiting.waiting.domain.entity.DailyWaiting;

public interface WaitingRepository {

  DailyWaiting getDailyWaitingBy(String dailyWaitingUuid);

  DailyWaiting save(DailyWaiting dailyWaiting);
}
