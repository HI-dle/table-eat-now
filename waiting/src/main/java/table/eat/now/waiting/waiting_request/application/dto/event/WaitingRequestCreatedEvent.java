package table.eat.now.waiting.waiting_request.application.dto.event;

import lombok.Builder;

@Builder
public record WaitingRequestCreatedEvent(
    String phone,
    String slackId,
    String restaurantName,
    Long sequence,
    Long remainingCount,
    long estimatedWaitingMin
) {
  public static WaitingRequestCreatedEvent of(
      String phone, String slackId, String restaurantName,
      Long sequence, Long remainingCount, long estimatedWaitingSec) {
    return WaitingRequestCreatedEvent.builder()
        .phone(phone)
        .slackId(slackId)
        .restaurantName(restaurantName)
        .sequence(sequence)
        .remainingCount(remainingCount)
        .estimatedWaitingMin(estimatedWaitingSec/60)
        .build();
  }
}
