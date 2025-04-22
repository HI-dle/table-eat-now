package table.eat.now.waiting.waiting_request.application.messaging;

public interface EventPublisher<T> {

  void publish(T event);
}
