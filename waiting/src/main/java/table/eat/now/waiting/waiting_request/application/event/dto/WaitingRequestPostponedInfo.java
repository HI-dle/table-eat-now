package table.eat.now.waiting.waiting_request.application.event.dto;

import lombok.Builder;

@Builder
public record WaitingRequestPostponedInfo(
    String waitingRequestUuid,
    String phone,
    String slackId,
    String restaurantName,
    Long sequence,
    Long remainingCount,
    long estimatedWaitingMin
) {
  public static WaitingRequestPostponedInfo of(
      String waitingRequestUuid, String phone, String slackId, String restaurantName,
      Long sequence, Long remainingCount, long estimatedWaitingSec) {

    return WaitingRequestPostponedInfo.builder()
        .waitingRequestUuid(waitingRequestUuid)
        .phone(phone)
        .slackId(slackId)
        .restaurantName(restaurantName)
        .sequence(sequence)
        .remainingCount(remainingCount)
        .estimatedWaitingMin(estimatedWaitingSec/60)
        .build();
  }
}
