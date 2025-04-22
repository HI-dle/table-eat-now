package table.eat.now.waiting.waiting_request.application.messaging.dto;

import lombok.Builder;

@Builder
public record WaitingRequestPostponedEvent(
    String waitingRequestUuid,
    EventType eventType,
    WaitingRequestPostponedInfo payload
) implements WaitingRequestEvent  {

  public static WaitingRequestPostponedEvent from(WaitingRequestPostponedInfo info) {
    return WaitingRequestPostponedEvent.builder()
        .waitingRequestUuid(info.waitingRequestUuid())
        .eventType(EventType.WAITING_POSTPONED)
        .payload(info)
        .build();
  }
}
