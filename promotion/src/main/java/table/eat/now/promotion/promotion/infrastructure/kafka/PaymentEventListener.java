package table.eat.now.promotion.promotion.infrastructure.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import table.eat.now.payment.payment.infrastructure.kafka.event.PaymentSuccessEvent;
import table.eat.now.promotion.promotion.application.service.PromotionService;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentEventListener {

  private final PromotionService promotionService;

  @KafkaListener(
      topics = "payment-event",
      containerFactory = "successEventKafkaListenerContainerFactory"
  )
  public void handlePromotionTest(PaymentSuccessEvent successEvent) {
    log.info("Processing payment success event: {}", successEvent.paymentUuid());
    log.info("success event payload: {}", successEvent.payload());
    log.info("success payload.paymentUuid: {}:", successEvent.payload().paymentUuid());
  }

}