package table.eat.now.waiting.waiting_request.application.messaging.dto;

public interface WaitingRequestEvent {
  String waitingRequestUuid();
  EventType eventType();
}
