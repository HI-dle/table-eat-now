package table.eat.now.promotion.promotion.infrastructure.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
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
  private static final String PROMOTION_TOPIC_DLT = "promotion-event-dlt";
  private static final String PROMOTION_TOPIC_NAME = "promotion-event";


  @KafkaListener(
      topics = PROMOTION_TOPIC_NAME,
      containerFactory = "promotionScheduleEventKafkaListenerContainerFactory"
  )
  public void handlePromotionSchedule(PromotionScheduleEvent event) {
    try {
      Promotion promotion = promotionRepository.findByPromotionUuidAndDeletedByIsNull(
              event.payload().promotionUuid())
          .orElseThrow(() ->
              CustomException.from(PromotionErrorCode.INVALID_PROMOTION_UUID));
      promotion.modifyPromotionStatus(promotion.getPromotionStatus());

      promotionRepository.save(promotion);
    } catch (Throwable e) {
      log.error("프로모션 상태 변경 중 에러 발생 발생 ID:{} {}", event.payload().promotionUuid(), e.getMessage());
      throw e;
    }
  }

  @KafkaListener(
      topics = PROMOTION_TOPIC_DLT,
      containerFactory = "promotionScheduleEventDltKafkaListenerContainerFactory"
  )
  public void handlePromotionScheduleDlt(
      ConsumerRecord<String, PromotionScheduleEvent> record,
      Acknowledgment ack,
      @Header(KafkaHeaders.RECEIVED_PARTITION) int partitionId,
      @Header(KafkaHeaders.OFFSET) Long offset,
      @Header(value = KafkaHeaders.EXCEPTION_MESSAGE, required = false) String errorMessage) {

    log.info("프로모션 상태 처리 이벤트 DLT 처리: key: {}, value: {}, partition : {}, offset : {}, errorMessage : {}",
        record.key(), record.value(), partitionId, offset, errorMessage);
    try {
      Promotion promotion = promotionRepository.findByPromotionUuidAndDeletedByIsNull(
              record.value().payload().promotionUuid())
          .orElseThrow(() ->
              CustomException.from(PromotionErrorCode.INVALID_PROMOTION_UUID));
      promotion.modifyPromotionStatus(promotion.getPromotionStatus());

      promotionRepository.save(promotion);
      ack.acknowledge();
    } catch (Throwable e) {
      log.error("프로모션 상태 처리 이벤트 DLT 처리 실패::수동 처리 필요:: "
              + "key: {}, value: {}, partition : {}, offset : {}, errorMessage : {}",
          record.key(), record.value(), partitionId, offset, errorMessage);
    } finally {
      ack.acknowledge();
    }
  }
}