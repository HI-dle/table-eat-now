package table.eat.now.waiting.waiting_request.presentation.dto.response;

import lombok.Builder;
import table.eat.now.waiting.waiting_request.application.dto.response.GetWaitingRequestInfo;

@Builder
public record GetWaitingRequestResponse(
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

  public static GetWaitingRequestResponse from(GetWaitingRequestInfo info) {
    return GetWaitingRequestResponse.builder()
        .waitingRequestUuid(info.waitingRequestUuid())
        .dailyWaitingUuid(info.dailyWaitingUuid())
        .restaurantUuid(info.restaurantUuid())
        .restaurantName(info.restaurantName())
        .phone(info.phone())
        .slackId(info.slackId())
        .seatSize(info.seatSize())
        .sequence(info.sequence())
        .rank(info.rank())
        .estimatedWaitingMin(info.estimatedWaitingMin())
        .build();
  }
}
