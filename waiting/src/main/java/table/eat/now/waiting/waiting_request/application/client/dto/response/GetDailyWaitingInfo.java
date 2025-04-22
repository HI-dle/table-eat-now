package table.eat.now.waiting.waiting_request.application.client.dto.response;

import java.time.LocalDate;
import lombok.Builder;

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

  public boolean isAvailable() {
    return status.equals(WaitingStatus.AVAILABLE.toString());
  }

  enum WaitingStatus {
    AVAILABLE,
    UNAVAILABLE
  }
}
