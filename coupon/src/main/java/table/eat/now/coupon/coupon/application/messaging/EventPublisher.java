package table.eat.now.coupon.coupon.application.messaging;

public interface EventPublisher<T> {

  void publish(T event);
}
