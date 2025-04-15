package table.eat.now.waiting.waiting_request.infrastructure.client.dto.response;

import java.time.LocalDate;
import lombok.Builder;
import table.eat.now.waiting.waiting_request.application.dto.response.GetDailyWaitingInfo;

@Builder
public record GetDailyWaitingResponse(
    String dailyWaitingUuid,
    String restaurantUuid,
    String restaurantName,
    String status,
    long avgWaitingSec,
    LocalDate waitingDate,
    Long totalSequence
) {

  public GetDailyWaitingInfo toInfo() {
    return GetDailyWaitingInfo.builder()
        .dailyWaitingUuid(dailyWaitingUuid)
        .restaurantUuid(restaurantUuid)
        .restaurantName(restaurantName)
        .status(status)
        .avgWaitingSec(avgWaitingSec)
        .waitingDate(waitingDate)
        .totalSequence(totalSequence)
        .build();
  }
}
