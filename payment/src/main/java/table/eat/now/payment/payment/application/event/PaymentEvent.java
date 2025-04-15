package table.eat.now.payment.payment.application.event;

public interface PaymentEvent {

  EventType eventType();

  String paymentUuid();
}
