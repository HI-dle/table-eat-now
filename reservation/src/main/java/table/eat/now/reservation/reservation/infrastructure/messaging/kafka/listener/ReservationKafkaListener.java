/**
 * @author : jieun(je-pa)
 * @Date : 2025. 04. 22.
 */
package table.eat.now.reservation.reservation.infrastructure.messaging.kafka.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import table.eat.now.reservation.reservation.application.service.ReservationService;
import table.eat.now.reservation.reservation.infrastructure.messaging.kafka.config.ReservationConsumerConfig.ListenerContainerFactoryName;
import table.eat.now.reservation.reservation.infrastructure.messaging.kafka.config.ReservationConsumerConfig.TopicName;
import table.eat.now.reservation.reservation.infrastructure.messaging.kafka.listener.dto.ReservationPaymentSucceedEvent;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReservationKafkaListener {
  private final ReservationService reservationService;

  @KafkaListener(
      topics = TopicName.PAYMENT_EVENT,
      containerFactory = ListenerContainerFactoryName.RESERVATION_PAYMENT_SUCCEED_EVENT
  )
  public void listenReservationPaymentSucceedEvent(
      ConsumerRecord<String, ReservationPaymentSucceedEvent> record, Acknowledgment ack) {
    log.info("예약 결제 성공 이벤트 처리: {}", record);
    try {
      ReservationPaymentSucceedEvent event = record.value();
      reservationService.confirmReservation(event.toCommand());
      ack.acknowledge();
    } catch (Throwable e) {
      log.warn("예약 결제 성공 이벤트 처리 예외 발생: {}", record.value(), e);
      throw e;
    }
  }

  @KafkaListener(
      topics = TopicName.PAYMENT_EVENT_DLT,
      containerFactory = ListenerContainerFactoryName.RESERVATION_PAYMENT_SUCCEED_EVENT_DLT
  )
  public void listenReservationCanceledEventDlt(
      ConsumerRecord<String, ReservationPaymentSucceedEvent> record,
      Acknowledgment ack,
      @Header(KafkaHeaders.RECEIVED_PARTITION) int partitionId,
      @Header(KafkaHeaders.OFFSET) Long offset,
      @Header(value = KafkaHeaders.EXCEPTION_MESSAGE, required = false) String errorMessage) {

    log.info("예약 결제 성공 이벤트 DLT 처리: key: {}, value: {}, partition : {}, offset : {}, errorMessage : {}",
        record.key(), record.value(), partitionId, offset, errorMessage);
    try {
      ReservationPaymentSucceedEvent event = record.value();
      reservationService.confirmReservation(event.toCommand());
    } catch (Throwable e) {
      log.error("예약 결제 성공 이벤트 DLT 처리 실패::수동 처리 필요:: "
              + "key: {}, value: {}, partition : {}, offset : {}, errorMessage : {}",
          record.key(), record.value(), partitionId, offset, errorMessage);
    } finally {
      ack.acknowledge();
    }
  }

}
