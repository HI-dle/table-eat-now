package table.eat.now.coupon.user_coupon.infrastructure.messaging.kafka;

import static table.eat.now.coupon.user_coupon.infrastructure.messaging.kafka.config.UserCouponConsumerConfig.RESERVATION_EVENT;
import static table.eat.now.coupon.user_coupon.infrastructure.messaging.kafka.config.UserCouponConsumerConfig.RESERVATION_EVENT_DLT;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import table.eat.now.coupon.user_coupon.application.service.UserCouponService;
import table.eat.now.coupon.user_coupon.infrastructure.messaging.kafka.dto.ReservationCancelledEvent;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserCouponKafkaEventListener {
  private final UserCouponService userCouponService;

  @KafkaListener(
      topics = RESERVATION_EVENT,
      containerFactory = "reservationCancelledEventKafkaListenerContainerFactory"
  )
  public void listenReservationCanceledEvent(
      ConsumerRecord<String, ReservationCancelledEvent> record, Acknowledgment ack) {
    log.info("예약 취소 이벤트 처리: {}", record);
    try {
      ReservationCancelledEvent canceledEvent = record.value();
      userCouponService.cancelUserCoupons(canceledEvent.reservationUuid());
      ack.acknowledge();
    } catch (Throwable e) {
      log.warn("예약 취소 이벤트 처리 예외 발생: {}", record.value(), e);
      throw e;
    }
  }

  @KafkaListener(
      topics = RESERVATION_EVENT_DLT,
      containerFactory = "reservationCancelledEventDltKafkaListenerContainerFactory"
  )
  public void listenReservationCanceledEventDlt(
      ConsumerRecord<String, ReservationCancelledEvent> record,
      Acknowledgment ack,
      @Header(KafkaHeaders.RECEIVED_PARTITION) int partitionId,
      @Header(KafkaHeaders.OFFSET) Long offset,
      @Header(value = KafkaHeaders.EXCEPTION_MESSAGE, required = false) String errorMessage) {

    log.info("예약 취소 이벤트 DLT 처리: key: {}, value: {}, partition : {}, offset : {}, errorMessage : {}",
        record.key(), record.value(), partitionId, offset, errorMessage);
    try {
      ReservationCancelledEvent canceledEvent = record.value();
      userCouponService.cancelUserCoupons(canceledEvent.reservationUuid());
      ack.acknowledge();
    } catch (Throwable e) {
      log.error("예약 취소 이벤트 DLT 처리 실패::수동 처리 필요:: "
          + "key: {}, value: {}, partition : {}, offset : {}, errorMessage : {}",
          record.key(), record.value(), partitionId, offset, errorMessage);
    } finally {
      ack.acknowledge();
    }
  }
}
