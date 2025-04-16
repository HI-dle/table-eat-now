package table.eat.now.notification.application.schedule;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import table.eat.now.notification.application.strategy.NotificationFormatterStrategy;
import table.eat.now.notification.application.strategy.NotificationFormatterStrategySelector;
import table.eat.now.notification.application.strategy.NotificationParamExtractor;
import table.eat.now.notification.application.strategy.NotificationTemplate;
import table.eat.now.notification.application.strategy.send.NotificationSenderStrategy;
import table.eat.now.notification.application.strategy.send.NotificationSenderStrategySelector;
import table.eat.now.notification.domain.entity.Notification;
import table.eat.now.notification.domain.entity.NotificationStatus;
import table.eat.now.notification.domain.repository.NotificationRepository;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 16.
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class NotificationScheduler {

  private final NotificationRepository notificationRepository;
  private final NotificationFormatterStrategySelector formatterSelector;
  private final NotificationSenderStrategySelector sendSelector;
  private final NotificationParamExtractor paramExtractor;

  @Scheduled(cron = "0 */10 * * * *")
  @Transactional
  public void sendScheduledNotifications() {
    LocalDateTime now = LocalDateTime.now();

    List<Notification> notifications = notificationRepository.
        findByStatusAndScheduledTimeLessThanEqualAndDeletedByIsNull(
        NotificationStatus.PENDING, now);

    for (Notification notification : notifications) {
      try {
        NotificationFormatterStrategy strategy = formatterSelector.select(
            notification.getNotificationType());
        Map<String, String> params = paramExtractor.extract(notification);
        NotificationTemplate formattedMessage = strategy.format(params);

        NotificationSenderStrategy senderStrategy = sendSelector.select(
            notification.getNotificationMethod());

        senderStrategy.send(notification.getUserId(), formattedMessage);

        notification.modifyNotificationStatusIsSent();

      } catch (Exception e) {
        log.error("스케줄링 알림 전송 실패: {}", notification.getId(), e);
        notification.modifyNotificationStatusIsFailed();
      }
    }
  }
}
