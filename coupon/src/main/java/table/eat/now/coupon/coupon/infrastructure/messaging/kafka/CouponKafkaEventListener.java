package table.eat.now.coupon.coupon.infrastructure.messaging.kafka;

import static table.eat.now.coupon.coupon.infrastructure.messaging.kafka.config.CouponConsumerConfig.PROMOTION_EVENT;
import static table.eat.now.coupon.coupon.infrastructure.messaging.kafka.config.CouponConsumerConfig.PROMOTION_EVENT_DLT;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import table.eat.now.coupon.coupon.application.usecase.IssuePromotionCouponUsecase;
import table.eat.now.coupon.coupon.infrastructure.messaging.kafka.dto.PromotionParticipatedCouponEvent;

@Slf4j
@Component
@RequiredArgsConstructor
public class CouponKafkaEventListener {
  private final IssuePromotionCouponUsecase issuePromotionCouponUsecase;

  @KafkaListener(
      topics = PROMOTION_EVENT,
      containerFactory = "promotionParticipatedEventKafkaListenerContainerFactory"
  )
  public void listenPromotionParticipatedCouponEvent(
      ConsumerRecord<String, PromotionParticipatedCouponEvent> record,
      Acknowledgment ack
  ) {
    log.info("프로모션 참여 완료 쿠폰 발급 이벤트 처리: {}", record);

    try {
      PromotionParticipatedCouponEvent participatedCouponEvent = record.value();
      Long timestamp = record.timestamp();

      issuePromotionCouponUsecase.execute(participatedCouponEvent.toCommand(timestamp));
      ack.acknowledge();
    } catch (Throwable e) {

      log.warn("프로모션 참여 완료 쿠폰 발급 이벤트 처리 예외 발생: {}", record.value(), e);
      throw e;
    }
  }

  @KafkaListener(
      topics = PROMOTION_EVENT_DLT,
      containerFactory = "promotionParticipatedEventDltKafkaListenerContainerFactory"
  )
  public void listenPromotionParticipatedCouponEventDlt(
      ConsumerRecord<String, PromotionParticipatedCouponEvent> record,
      Acknowledgment ack,
      @Header(KafkaHeaders.RECEIVED_PARTITION) int partitionId,
      @Header(KafkaHeaders.OFFSET) Long offset,
      @Header(value = KafkaHeaders.EXCEPTION_MESSAGE, required = false) String errorMessage) {

    log.info("프로모션 참여 완료 쿠폰 발급 이벤트 DLT 처리: key: {}, value: {}, partition : {}, offset : {}, errorMessage : {}",
        record.key(), record.value(), partitionId, offset, errorMessage);

    try {
      PromotionParticipatedCouponEvent participatedCouponEvent = record.value();
      Long timestamp = record.timestamp();

      issuePromotionCouponUsecase.execute(participatedCouponEvent.toCommand(timestamp));
      ack.acknowledge();
    } catch (Throwable e) {

      log.error("프로모션 참여 완료 쿠폰 발급 이벤트 DLT 처리 실패::수동 처리 필요:: "
          + "key: {}, value: {}, partition : {}, offset : {}, errorMessage : {}",
          record.key(), record.value(), partitionId, offset, errorMessage);
    } finally {
      ack.acknowledge();
    }
  }
}
