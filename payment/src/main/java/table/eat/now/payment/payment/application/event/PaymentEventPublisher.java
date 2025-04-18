package table.eat.now.payment.payment.application.event;

public interface PaymentEventPublisher {

  void publish(PaymentSuccessEvent createdEvent);

  void publish(PaymentFailedEvent failedEvent);

  void publish(PaymentCanceledEvent canceledEvent);
}
