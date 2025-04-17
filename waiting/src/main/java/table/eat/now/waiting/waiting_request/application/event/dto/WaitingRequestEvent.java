package table.eat.now.waiting.waiting_request.application.event.dto;

public interface WaitingRequestEvent {
  String waitingRequestUuid();
  EventType eventType();
}
