package table.eat.now.waiting.waiting_request.application.event.dto;

import lombok.Builder;

@Builder
public record WaitingRequestCreatedInfo(
    String waitingRequestUuid,
    String phone,
    String slackId,
    String restaurantName,
    Long sequence,
    Long remainingCount,
    long estimatedWaitingMin
) {
  public static WaitingRequestCreatedInfo of(
      String waitingRequestUuid, String phone, String slackId, String restaurantName,
      Long sequence, Long remainingCount, long estimatedWaitingSec) {

    return WaitingRequestCreatedInfo.builder()
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
