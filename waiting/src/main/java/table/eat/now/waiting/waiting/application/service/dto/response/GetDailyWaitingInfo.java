package table.eat.now.waiting.waiting.application.service.dto.response;

import java.time.LocalDate;
import lombok.Builder;
import table.eat.now.waiting.waiting.domain.entity.DailyWaiting;

@Builder
public record GetDailyWaitingInfo(
    String dailyWaitingUuid,
    String restaurantUuid,
    String restaurantName,
    String status,
    long avgWaitingSec,
    LocalDate waitingDate,
    Long totalSequence
) {

  public static GetDailyWaitingInfo from(DailyWaiting dailyWaiting) {
    return GetDailyWaitingInfo.builder()
        .dailyWaitingUuid(dailyWaiting.getDailyWaitingUuid())
        .restaurantUuid(dailyWaiting.getRestaurantUuid())
        .restaurantName(dailyWaiting.getRestaurantName())
        .status(dailyWaiting.getStatus().toString())
        .avgWaitingSec(dailyWaiting.getAvgWaitingSec())
        .waitingDate(dailyWaiting.getWaitingDate())
        .totalSequence(dailyWaiting.getTotalSequence())
        .build();
  }
}
