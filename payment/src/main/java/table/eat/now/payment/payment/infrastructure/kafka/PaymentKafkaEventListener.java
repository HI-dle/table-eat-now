package table.eat.now.payment.payment.infrastructure.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import table.eat.now.payment.payment.application.PaymentService;
import table.eat.now.payment.payment.application.metric.MetricName;
import table.eat.now.payment.payment.infrastructure.metric.MetricRecorder;
import table.eat.now.payment.payment.infrastructure.kafka.event.ReservationCancelledEvent;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentKafkaEventListener {

  private final PaymentService paymentService;
  private final MetricRecorder metricRecorder;

  @KafkaListener(
      topics = "reservation-event",
      containerFactory = "reservationCancelledEventListenerFactory"
  )
  public void handleReservationCanceled(
      ConsumerRecord<String, ReservationCancelledEvent> record,
      Acknowledgment ack,
      @Header(KafkaHeaders.RECEIVED_PARTITION) int partitionId,
      @Header(KafkaHeaders.OFFSET) Long offset) {

    log.info("예약 취소 이벤트 수신: partition={}, offset={}", partitionId, offset);

    metricRecorder.recordTime(MetricName.PAYMENT_KAFKA_RESERVATION_CANCEL.value(), () -> {
      try {
        ReservationCancelledEvent cancelEvent = record.value();
        paymentService.cancelPayment(cancelEvent.payload().toCommand(), cancelEvent.userInfo());
        handleReservationCancelSuccess(ack, cancelEvent);
      } catch (Exception e) {

        handleReservationCancelFailure(e);
      }
    });

  }

  private void handleReservationCancelSuccess(
      Acknowledgment ack, ReservationCancelledEvent cancelEvent) {
    ack.acknowledge();
    metricRecorder
        .countSuccess(MetricName.PAYMENT_KAFKA_RESERVATION_CANCEL.value());
    log.info("예약 취소에 따른 결제 취소 처리 완료: reservationId={}",
        cancelEvent.payload().reservationUuid());
  }

  private void handleReservationCancelFailure(Exception e) {
    metricRecorder.countFailure(MetricName.PAYMENT_KAFKA_RESERVATION_CANCEL.value());
    log.error("예약 취소에 따른 결제 취소 처리 실패: {}", e.getMessage());
    throw new RuntimeException(e);
  }

  @KafkaListener(
      topics = "reservation-event-dlt",
      containerFactory = "reservationCancelledEventDltListenerFactory"
  )
  public void handleReservationCanceledDlt(
      ConsumerRecord<String, ReservationCancelledEvent> record,
      Acknowledgment ack,
      @Header(KafkaHeaders.RECEIVED_PARTITION) int partitionId,
      @Header(KafkaHeaders.OFFSET) Long offset,
      @Header(value = KafkaHeaders.EXCEPTION_MESSAGE, required = false) String errorMessage) {

    log.warn("DLT에서 예약 취소 이벤트 수신: partition={}, offset={}, 원본 오류={}",
        partitionId, offset, errorMessage);

    metricRecorder.recordTime(MetricName.PAYMENT_KAFKA_RESERVATION_CANCEL_DLT.value(), () -> {
      try {
        ReservationCancelledEvent cancelEvent = record.value();
        paymentService.cancelPayment(cancelEvent.payload().toCommand(), cancelEvent.userInfo());
        handleReservationCancelDltSuccess(ack, cancelEvent);
      } catch (Exception e) {
        handleReservationCancelDltFailure(record, e);
      }
    });
  }

  private void handleReservationCancelDltSuccess(
      Acknowledgment ack, ReservationCancelledEvent cancelEvent) {
    ack.acknowledge();

    log.info("DLT 예약 취소에 따른 결제 취소 처리 완료: reservationId={}",
        cancelEvent.payload().restaurantUuid());
    metricRecorder.countSuccess(MetricName.PAYMENT_KAFKA_RESERVATION_CANCEL_DLT.value());
  }

  private void handleReservationCancelDltFailure(
      ConsumerRecord<String, ReservationCancelledEvent> record, Exception e) {
    metricRecorder.countFailure(MetricName.PAYMENT_KAFKA_RESERVATION_CANCEL_DLT.value());
    log.error("DLT 예약 취소 처리 최종 실패: reservationId={}, 오류={}",
        record.value().payload().reservationUuid(), e.getMessage());
    throw new RuntimeException(e);
  }
}