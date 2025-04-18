package table.eat.now.review.application.event;

public interface ReviewEvent {
  EventType eventType();
  String eventId();
}