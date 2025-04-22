package table.eat.now.waiting.waiting_request.application.client;


import table.eat.now.waiting.waiting_request.application.client.dto.response.GetDailyWaitingInfo;

public interface WaitingClient {

  GetDailyWaitingInfo getDailyWaitingInfo(String dailyWaitingUuid);
}
