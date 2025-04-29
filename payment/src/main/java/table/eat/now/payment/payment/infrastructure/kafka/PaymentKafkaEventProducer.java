package table.eat.now.payment.payment.infrastructure.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import table.eat.now.payment.payment.application.event.ReservationPaymentCancelledEvent;
import table.eat.now.payment.payment.application.event.PaymentEvent;
import table.eat.now.payment.payment.application.event.PaymentEventPublisher;
import table.eat.now.payment.payment.application.event.ReservationPaymentSucceedEvent;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentKafkaEventProducer implements PaymentEventPublisher {

  private final KafkaTemplate<String, PaymentEvent> kafkaTemplate;
  private final String paymentTopic;

  @Override
  public void publish(ReservationPaymentSucceedEvent successEvent) {
    kafkaTemplate.send(paymentTopic, successEvent.paymentUuid() ,successEvent);
    logEvent(successEvent);
  }

  @Override
  public void publish(ReservationPaymentCancelledEvent canceledEvent) {
    kafkaTemplate.send(paymentTopic, canceledEvent.paymentUuid() , canceledEvent);
    logEvent(canceledEvent);
  }

  private static void logEvent(PaymentEvent paymentEvent) {
    log.info("Published payment event {}", paymentEvent.eventType().name());
  }
}