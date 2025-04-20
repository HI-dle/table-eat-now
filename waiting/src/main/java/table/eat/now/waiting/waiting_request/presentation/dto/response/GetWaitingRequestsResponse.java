package table.eat.now.waiting.waiting_request.presentation.dto.response;

import java.util.List;
import lombok.Builder;
import table.eat.now.waiting.waiting_request.application.dto.response.GetWaitingRequestInfo;
import table.eat.now.waiting.waiting_request.application.dto.response.PageResult;

@Builder
public record GetWaitingRequestsResponse(
    List<GetWaitingRequestResponse> waitingRequests,
    long totalElements,
    int totalPages,
    int pageNumber,
    int pageSize
) {

  public static GetWaitingRequestsResponse from(PageResult<GetWaitingRequestInfo> info) {
    return GetWaitingRequestsResponse.builder()
        .waitingRequests(info.contents()
            .stream()
            .map(GetWaitingRequestResponse::from)
            .toList())
        .totalElements(info.totalElements())
        .totalPages(info.totalPages())
        .pageNumber(info.pageNumber())
        .pageSize(info.pageSize())
        .build();
  }

  @Builder
  record GetWaitingRequestResponse(
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

    public static GetWaitingRequestResponse from(
        GetWaitingRequestInfo info) {
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
}
