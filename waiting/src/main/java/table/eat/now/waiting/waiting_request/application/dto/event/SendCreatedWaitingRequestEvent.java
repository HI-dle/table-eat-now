package table.eat.now.waiting.waiting_request.application.dto.event;

import lombok.Builder;

@Builder
public record SendCreatedWaitingRequestEvent(
    String phone,
    String slackId,
    Long sequence,
    Long remainingCount,
    long estimatedWaitingTime
) {
  public static SendCreatedWaitingRequestEvent of(
      String phone, String slackId, Long sequence, Long remainingCount, long estimatedWaitingTime) {
    return SendCreatedWaitingRequestEvent.builder()
        .phone(phone)
        .slackId(slackId)
        .sequence(sequence)
        .remainingCount(remainingCount)
        .estimatedWaitingTime(estimatedWaitingTime)
        .build();
  }
}
