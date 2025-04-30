package table.eat.now.payment.payment.application.event;

public interface PaymentEventPublisher {

  void publish(ReservationPaymentSucceedEvent createdEvent);

  void publish(ReservationPaymentCancelledEvent canceledEvent);
}
