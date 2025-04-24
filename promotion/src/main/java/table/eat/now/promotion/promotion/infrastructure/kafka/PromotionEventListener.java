package table.eat.now.promotion.promotion.infrastructure.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import table.eat.now.common.exception.CustomException;
import table.eat.now.promotion.promotion.application.event.produce.PromotionScheduleEvent;
import table.eat.now.promotion.promotion.application.exception.PromotionErrorCode;
import table.eat.now.promotion.promotion.application.service.PromotionService;
import table.eat.now.promotion.promotion.domain.entity.Promotion;
import table.eat.now.promotion.promotion.domain.entity.repository.PromotionRepository;

@Slf4j
@Component
@RequiredArgsConstructor
public class PromotionEventListener {

  private final PromotionService promotionService;
  private final PromotionRepository promotionRepository;


  @KafkaListener(
      topics = "promotion-event",
      containerFactory = "promotionScheduleEventKafkaListenerContainerFactory"
  )
  public void handlePromotionSchedule(PromotionScheduleEvent event) {
    Promotion promotion = promotionRepository.findByPromotionUuidAndDeletedByIsNull(
            event.payload().promotionUuid())
        .orElseThrow(() ->
            CustomException.from(PromotionErrorCode.INVALID_PROMOTION_UUID));
    promotion.modifyPromotionStatus(promotion);

    promotionRepository.save(promotion);
  }

}