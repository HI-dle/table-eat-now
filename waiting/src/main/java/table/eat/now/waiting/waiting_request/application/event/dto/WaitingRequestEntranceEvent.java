package table.eat.now.waiting.waiting_request.application.event.dto;

import lombok.Builder;

@Builder
public record WaitingRequestEntranceEvent(
    String waitingRequestUuid,
    EventType eventType,
    WaitingRequestEntranceInfo payload
) implements WaitingRequestEvent {

  public static WaitingRequestEntranceEvent from(WaitingRequestEntranceInfo info) {
    return WaitingRequestEntranceEvent.builder()
        .waitingRequestUuid(info.waitingRequestUuid())
        .eventType(EventType.WAITING_ENTRANCE)
        .payload(info)
        .build();
  }
}
