package table.eat.now.waiting.waiting_request.application.dto.response;

import lombok.Builder;
import table.eat.now.waiting.waiting_request.domain.entity.WaitingRequest;

@Builder
public record GetWaitingRequestInfo(
    String waitingRequestUuid,
    String dailyWaitingUuid,
    String restaurantUuid,
    String restaurantName,
    String phone,
    String slackId,
    int seatSize,
    Integer sequence,
    Long rank,
    long estimatedWaitingMin
) {

  public static GetWaitingRequestInfo from(
      WaitingRequest waitingRequest, String restaurantName, Long rank, long estimatedWaitingSec) {
    return GetWaitingRequestInfo.builder()
        .waitingRequestUuid(waitingRequest.getWaitingRequestUuid())
        .dailyWaitingUuid(waitingRequest.getDailyWaitingUuid())
        .restaurantUuid(waitingRequest.getRestaurantUuid())
        .restaurantName(restaurantName)
        .phone(waitingRequest.getPhone())
        .slackId(waitingRequest.getSlackId())
        .seatSize(waitingRequest.getSeatSize())
        .sequence(waitingRequest.getSequence())
        .rank(rank)
        .estimatedWaitingMin(estimatedWaitingSec/60)
        .build();
  }
}
