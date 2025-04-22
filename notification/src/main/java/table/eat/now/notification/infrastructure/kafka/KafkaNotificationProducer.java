package table.eat.now.notification.infrastructure.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import table.eat.now.notification.application.event.NotificationEvent;
import table.eat.now.notification.application.event.NotificationEventPublisher;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaNotificationProducer implements NotificationEventPublisher {

  private final KafkaTemplate<String, NotificationEvent> kafkaTemplate;
  private final String notificationTopic;


  @Override
  public void publish(NotificationEvent event) {
    kafkaTemplate.send(notificationTopic, event);
    logEvent(event);
  }

  private static void logEvent(NotificationEvent notificationEvent) {
    log.info("Published notification event {}", notificationEvent.eventType().name());
  }
}