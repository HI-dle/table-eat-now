package table.eat.now.promotion.promotion.infrastructure.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import table.eat.now.promotion.promotion.application.event.PromotionEvent;
import table.eat.now.promotion.promotion.application.event.PromotionEventPublisher;
import table.eat.now.promotion.promotion.application.event.produce.PromotionScheduleEvent;
import table.eat.now.promotion.promotion.application.event.produce.PromotionUserCouponSaveEvent;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaPromotionProducer implements PromotionEventPublisher {

  private final KafkaTemplate<String, PromotionEvent> kafkaTemplate;
  private final String promotionTopic;


  @Override
  public void publish(PromotionEvent event) {
    kafkaTemplate.send(promotionTopic, event);
    logEvent(event);
  }

  @Override
  public void publish(PromotionUserCouponSaveEvent event) {
    kafkaTemplate.send(promotionTopic, event);
    logEvent(event);
  }

  @Override
  public void publish(PromotionScheduleEvent event) {
    kafkaTemplate.send(promotionTopic, event);
    logEvent(event);
  }


  private static void logEvent(PromotionEvent promotionEvent) {
    log.info("Published promotion event {}", promotionEvent.eventType().name());
  }
}