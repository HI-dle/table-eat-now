package table.eat.now.notification.infrastructure.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import table.eat.now.notification.application.event.produce.NotificationScheduleSendEvent;
import table.eat.now.notification.application.event.produce.NotificationSendEvent;
import table.eat.now.notification.application.service.NotificationService;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationEventListener {

  private final NotificationService notificationService;

  @KafkaListener(
      topics = "notification-event",
      containerFactory = "sendNotificationEventKafkaListenerContainerFactory"
  )
  public void handleNotificationSend(NotificationSendEvent notificationSendEvent) {
    notificationService.consumerNotification(notificationSendEvent);
  }

  @KafkaListener(
      topics = "notification-event",
      containerFactory = "ScheduleSendNotificationEventKafkaListenerContainerFactory"
  )
  public void handleNotificationSend(NotificationScheduleSendEvent event) {
    notificationService.consumerScheduleSendNotification(event);
  }
}