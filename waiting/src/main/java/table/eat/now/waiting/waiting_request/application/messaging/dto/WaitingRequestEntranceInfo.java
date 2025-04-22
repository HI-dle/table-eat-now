package table.eat.now.waiting.waiting_request.application.messaging.dto;

import lombok.Builder;

@Builder
public record WaitingRequestEntranceInfo(
    String waitingRequestUuid,
    String phone,
    String slackId,
    String restaurantName,
    Long sequence
) {
  public static WaitingRequestEntranceInfo of(
      String waitingRequestUuid, String phone, String slackId, String restaurantName, Long sequence)
  {
    return WaitingRequestEntranceInfo.builder()
        .waitingRequestUuid(waitingRequestUuid)
        .phone(phone)
        .slackId(slackId)
        .restaurantName(restaurantName)
        .sequence(sequence)
        .build();
  }
}
