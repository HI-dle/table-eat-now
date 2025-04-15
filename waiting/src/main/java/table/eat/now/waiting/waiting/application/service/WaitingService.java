package table.eat.now.waiting.waiting.application.service;

import table.eat.now.waiting.waiting.application.service.dto.response.GetDailyWaitingInfo;

public interface WaitingService {

  GetDailyWaitingInfo getDailyWaitingInfo(String dailyWaitingUuid);
}
