package table.eat.now.notification.infrastructure.kafka;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import table.eat.now.notification.application.event.produce.NotificationScheduleSendEvent;
import table.eat.now.notification.application.event.produce.NotificationSendEvent;
import table.eat.now.notification.application.service.NotificationService;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationEventListener {

  private static final String NOTIFICATION_TOPIC_NAME = "notification-event";
  private final NotificationService notificationService;

  @KafkaListener(
      topics = NOTIFICATION_TOPIC_NAME,
      containerFactory = "sendNotificationEventKafkaListenerContainerFactory"
  )
  public void handleNotificationSend(NotificationSendEvent notificationSendEvent) {
    try {
      notificationService.consumerNotification(notificationSendEvent);
    } catch (Throwable e) {
      log.error("알림 전송 이벤트 에러 발생 {}", e.getMessage());
      throw e;
    }
  }

  @KafkaListener(
      topics = NOTIFICATION_TOPIC_NAME,
      containerFactory = "scheduleSendNotificationEventKafkaListenerContainerFactory"
  )
  public void handleNotificationScheduleSend(NotificationScheduleSendEvent event) {
    try {
      notificationService.consumerScheduleSendNotification(event);
    } catch (Throwable e) {
      log.error("알림 스케줄 전송 이벤트 에러 발생 {}", e.getMessage());
      throw e;
    }
  }
  @KafkaListener(
      topics = "Notification-event-dlt",
      containerFactory = "notificationSendEventDltKafkaListenerContainerFactory"
  )
  public void handleNotificationSendDlt(
      NotificationSendEvent event,
      Acknowledgment ack,
      @Header(KafkaHeaders.RECEIVED_PARTITION) int partitionId,
      @Header(KafkaHeaders.OFFSET) Long offset,
      @Header(value = KafkaHeaders.EXCEPTION_MESSAGE, required = false) String errorMessage) {
    log.warn("NotificationSendEvent DLT 처리 시작 - partition: {}, offset: {}, errorMessage: {}",
        partitionId, offset, errorMessage);

    try {
      notificationService.consumerNotification(event);
    } catch (Throwable e) {
      log.error("NotificationSendEvent DLT 처리 실패 - 수동 처리 필요 - partition: {}, offset: {}, error: {}",
          partitionId, offset, e.getMessage(), e);
    } finally {
      ack.acknowledge();
    }
  }
  @KafkaListener(
      topics = "Notification-schedule-event-dlt",
      containerFactory = "notificationScheduleSendEventDltKafkaListenerContainerFactory"
  )
  public void handleNotificationScheduleSendDlt(
      NotificationScheduleSendEvent event,
      Acknowledgment ack,
      @Header(KafkaHeaders.RECEIVED_PARTITION) int partitionId,
      @Header(KafkaHeaders.OFFSET) Long offset,
      @Header(value = KafkaHeaders.EXCEPTION_MESSAGE, required = false) String errorMessage
  ) {
    log.warn("NotificationScheduleSendEvent DLT 처리 시작 - partition: {}, offset: {}, errorMessage: {}",
        partitionId, offset, errorMessage);

    try {
      notificationService.consumerScheduleSendNotification(event);
    } catch (Throwable e) {
      log.error("NotificationScheduleSendEvent DLT 처리 실패 - 수동 처리 필요 - partition: {}, offset: {}, error: {}",
          partitionId, offset, e.getMessage(), e);
    } finally {
      ack.acknowledge();
    }
  }

}