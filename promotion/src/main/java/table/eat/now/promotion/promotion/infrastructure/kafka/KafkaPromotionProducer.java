package table.eat.now.promotion.promotion.infrastructure.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import table.eat.now.promotion.promotion.application.event.PromotionEvent;
import table.eat.now.promotion.promotion.application.event.PromotionEventPublisher;
import table.eat.now.promotion.promotion.application.event.produce.PromotionUserSaveEvent;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaPromotionProducer implements PromotionEventPublisher {

  private final KafkaTemplate<String, PromotionEvent> kafkaTemplate;
  private final String promotionTopic;


  @Override
  public void publish(PromotionUserSaveEvent userSaveEvent) {
    kafkaTemplate.send(promotionTopic, userSaveEvent);
    logEvent(userSaveEvent);
  }

  private static void logEvent(PromotionEvent promotionEvent) {
    log.info("Published promotion event {}", promotionEvent.eventType().name());
  }
}