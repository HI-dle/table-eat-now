package table.eat.now.payment.payment.application.event;

import com.fasterxml.jackson.databind.JsonNode;

public interface PaymentEvent {

  EventType eventType();

  String paymentUuid();

  JsonNode payload();
}
