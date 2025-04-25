package table.eat.now.promotion.promotionuser.infrastructure.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import table.eat.now.promotion.promotionuser.application.service.PromotionUserService;
import table.eat.now.promotion.promotionuser.infrastructure.kafka.dto.PromotionUserSaveEvent;

@Slf4j
@Component
@RequiredArgsConstructor
public class PromotionUserEventListener {

  private final PromotionUserService promotionUserService;
  private static final String PROMOTION_TOPIC_DLT = "promotion-event-dlt";

  @KafkaListener(
      topics = "promotion-event",
      containerFactory = "createPromotionUserEventKafkaListenerContainerFactory"
  )
  public void handlePromotionUserSave(PromotionUserSaveEvent promotionUserSaveEvent) {
    try {
      promotionUserService.savePromotionUsers(
          PromotionUserSaveEvent.toApplication(promotionUserSaveEvent));
    } catch (Throwable e) {
      log.error("프로모션 참여 유저 저장 이벤트 에러 발생 {}", e.getMessage());
      throw e;
    }
  }

  @KafkaListener(
      topics = PROMOTION_TOPIC_DLT,
      containerFactory = "promotionUserEventDltKafkaListenerContainerFactory"
  )
  public void handlePromotionUserSaveDlt(
      PromotionUserSaveEvent event,
      Acknowledgment ack,
      @Header(KafkaHeaders.RECEIVED_PARTITION) int partitionId,
      @Header(KafkaHeaders.OFFSET) Long offset,
      @Header(value = KafkaHeaders.EXCEPTION_MESSAGE, required = false) String errorMessage) {

    log.info("프로모션 참여 유저 저장 이벤트 DLT 처리: partition : {}, offset : {}, errorMessage : {}",
           partitionId, offset, errorMessage);
    try {
      promotionUserService.savePromotionUsers(
          PromotionUserSaveEvent.toApplication(event));
    }  catch (Throwable e) {
      log.error("프로모션 상태 처리 이벤트 DLT 처리 실패::수동 처리 필요:: "
              + " partition : {}, offset : {}, errorMessage : {}", partitionId, offset, errorMessage);
    } finally {
      ack.acknowledge();
    }
  }

}