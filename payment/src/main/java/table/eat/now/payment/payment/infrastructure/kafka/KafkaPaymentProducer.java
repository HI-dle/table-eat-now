package table.eat.now.payment.payment.infrastructure.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import table.eat.now.payment.payment.application.event.PaymentEvent;
import table.eat.now.payment.payment.application.event.PaymentEventPublisher;
import table.eat.now.payment.payment.application.event.PaymentSuccessEvent;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaPaymentProducer implements PaymentEventPublisher {

  private final KafkaTemplate<String, PaymentEvent> kafkaTemplate;
  private final String paymentTopic;

  @Override
  public void publish(PaymentSuccessEvent paymentEvent) {
    kafkaTemplate.send(paymentTopic, paymentEvent.paymentUuid() ,paymentEvent);
    log.info("payload is {} ", paymentEvent.payload().toString());
    log.info("Published payment event {}", paymentEvent.eventType());
  }
}