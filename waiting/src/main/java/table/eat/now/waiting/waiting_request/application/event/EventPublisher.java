package table.eat.now.waiting.waiting_request.application.event;

public interface EventPublisher<T> {

  void publish(T event);
}
