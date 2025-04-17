package table.eat.now.promotion.promotionuser.infrastructure.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import table.eat.now.promotion.promotionuser.application.service.PromotionUserService;
import table.eat.now.promotion.promotionuser.infrastructure.kafka.dto.PromotionUserSaveEvent;

@Slf4j
@Component
@RequiredArgsConstructor
public class PromotionUserEventListener {

  private final PromotionUserService promotionUserService;

  @KafkaListener(
      topics = "promotion-event",
      containerFactory = "createPromotionUserEventKafkaListenerContainerFactory"
  )
  public void handlePromotionUserSave(PromotionUserSaveEvent promotionUserSaveEvent) {
    promotionUserService.savePromotionUsers(
        PromotionUserSaveEvent.toApplication(promotionUserSaveEvent));
  }

}