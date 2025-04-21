package table.eat.now.review.infrastructure.client.dto.response;

import table.eat.now.review.application.client.dto.GetServiceInfo;

public record GetWaitingRequestResponse(
    String waitingRequestUuid,
    String dailyWaitingUuid,
    String restaurantUuid,
    String restaurantName,
    Long userId,
    String phone,
    String slackId,
    int seatSize,
    String status,
    Integer sequence,
    Long rank,
    Long estimatedWaitingMin
) {
  public GetServiceInfo toInfo() {
    return new GetServiceInfo(
        waitingRequestUuid,
        userId
    );
  }
}
