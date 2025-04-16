package table.eat.now.waiting.waiting_request.application.event.dto;

import lombok.Builder;

@Builder
public record WaitingRequestCreatedEvent(
    String waitingRequestUuid,
    EventType eventType,
    WaitingRequestCreatedInfo payload
) implements WaitingRequestEvent  {

  public static WaitingRequestCreatedEvent from(WaitingRequestCreatedInfo info) {
    return WaitingRequestCreatedEvent.builder()
        .waitingRequestUuid(info.waitingRequestUuid())
        .eventType(EventType.WAITING_CREATED)
        .payload(info)
        .build();
  }
}
