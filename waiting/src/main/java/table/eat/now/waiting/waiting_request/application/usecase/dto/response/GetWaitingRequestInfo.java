package table.eat.now.waiting.waiting_request.application.usecase.dto.response;

import lombok.Builder;
import table.eat.now.waiting.waiting_request.domain.entity.WaitingRequest;

@Builder
public record GetWaitingRequestInfo(
    String waitingRequestUuid,
    String dailyWaitingUuid,
    String restaurantUuid,
    String restaurantName,
    Long userId,
    String phone,
    String slackId,
    int seatSize,
    Integer sequence,
    String status,
    Long rank,
    Long estimatedWaitingMin
) {

  public static GetWaitingRequestInfo from(
      WaitingRequest waitingRequest, String restaurantName, Long rank, Long estimatedWaitingMin) {
    return GetWaitingRequestInfo.builder()
        .waitingRequestUuid(waitingRequest.getWaitingRequestUuid())
        .dailyWaitingUuid(waitingRequest.getDailyWaitingUuid())
        .restaurantUuid(waitingRequest.getRestaurantUuid())
        .restaurantName(restaurantName)
        .userId(waitingRequest.getUserId())
        .phone(waitingRequest.getPhone())
        .slackId(waitingRequest.getSlackId())
        .seatSize(waitingRequest.getSeatSize())
        .sequence(waitingRequest.getSequence())
        .status(waitingRequest.getStatus().toString())
        .rank(rank)
        .estimatedWaitingMin(estimatedWaitingMin)
        .build();
  }
}
