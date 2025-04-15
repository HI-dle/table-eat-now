package table.eat.now.payment.payment.infrastructure.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import table.eat.now.payment.payment.infrastructure.kafka.event.PaymentFailedEvent;
import table.eat.now.payment.payment.infrastructure.kafka.event.PaymentSuccessEvent;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentEventListener {

  @KafkaListener(
      topics = "payment-event",
      containerFactory = "successEventKafkaListenerContainerFactory"
  )
  public void handlePaymentSuccess(PaymentSuccessEvent successEvent) {
    log.info("Processing payment success event: {}", successEvent.paymentUuid());
    log.info("success event payload: {}", successEvent.payload());
    log.info("success payload.paymentUuid: {}:", successEvent.payload().paymentUuid());
  }

  @KafkaListener(
      topics = "payment-event",
      containerFactory = "failedEventKafkaListenerContainerFactory"
  )
  public void handlePaymentFailed(PaymentFailedEvent failedEvent) {
    log.info("Processing payment failed event: {}", failedEvent.paymentUuid());
    log.info("failed event payload: {}", failedEvent.payload());
  }
}