package table.eat.now.payment.payment.infrastructure.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import table.eat.now.payment.payment.application.event.PaymentCanceledEvent;
import table.eat.now.payment.payment.application.event.PaymentEvent;
import table.eat.now.payment.payment.application.event.PaymentEventPublisher;
import table.eat.now.payment.payment.application.event.PaymentFailedEvent;
import table.eat.now.payment.payment.application.event.PaymentSuccessEvent;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaPaymentProducer implements PaymentEventPublisher {

  private final KafkaTemplate<String, PaymentEvent> kafkaTemplate;
  private final String paymentTopic;

  @Override
  public void publish(PaymentSuccessEvent successEvent) {
    kafkaTemplate.send(paymentTopic, successEvent.paymentUuid() ,successEvent);
    logEvent(successEvent);
  }

  @Override
  public void publish(PaymentFailedEvent failedEvent) {
    kafkaTemplate.send(paymentTopic, failedEvent.paymentUuid() ,failedEvent);
    logEvent(failedEvent);
  }

  @Override
  public void publish(PaymentCanceledEvent canceledEvent) {
    kafkaTemplate.send(paymentTopic, canceledEvent.paymentUuid() , canceledEvent);
    logEvent(canceledEvent);
  }

  private static void logEvent(PaymentEvent paymentEvent) {
    log.info("Published payment event {}", paymentEvent.eventType().name());
  }
}