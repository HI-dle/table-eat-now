package table.eat.now.notification.application.schedule;

import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import table.eat.now.notification.application.event.NotificationEventPublisher;
import table.eat.now.notification.application.event.produce.NotificationScheduleSendEvent;
import table.eat.now.notification.application.metric.NotificationMetrics;
import table.eat.now.notification.domain.entity.Notification;
import table.eat.now.notification.domain.repository.NotificationRepository;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 23.
 */
@RequiredArgsConstructor
@Service
@Slf4j
public class NotificationDelayQueueScheduler {

  private final NotificationRepository notificationRepository;
  private final NotificationEventPublisher publisher;
  private final NotificationMetrics metrics;

  @Value("${scheduler.max-dispatch-count}")
  private int maxDispatchCount;

  @Scheduled(fixedDelay = 1000)
  @Transactional
  public void pollAndDispatch() {
    List<String> notificationIds = notificationRepository.popDueNotifications(maxDispatchCount);

    if (notificationIds.isEmpty()) {
      log.debug("발송할 알림이 없습니다.");
      return;
    }
    metrics.recordSchedulerExecution(() -> {
      List<Notification> notifications = notificationRepository
          .findByNotificationUuidIn(notificationIds);

      metrics.recordFetchedScheduledCount(notifications.size());

      for (Notification notification : notifications) {
        publisher.publish(NotificationScheduleSendEvent.from(notification));
      }
    });
  }

}
