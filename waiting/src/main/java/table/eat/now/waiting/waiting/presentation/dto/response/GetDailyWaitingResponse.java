package table.eat.now.waiting.waiting.presentation.dto.response;

import java.time.LocalDate;
import lombok.Builder;
import table.eat.now.waiting.waiting.application.service.dto.response.GetDailyWaitingInfo;

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

  public static GetDailyWaitingResponse from(GetDailyWaitingInfo info) {
    return GetDailyWaitingResponse.builder()
        .dailyWaitingUuid(info.dailyWaitingUuid())
        .restaurantUuid(info.restaurantUuid())
        .restaurantName(info.restaurantName())
        .status(info.status())
        .avgWaitingSec(info.avgWaitingSec())
        .waitingDate(info.waitingDate())
        .totalSequence(info.totalSequence())
        .build();
  }
}
