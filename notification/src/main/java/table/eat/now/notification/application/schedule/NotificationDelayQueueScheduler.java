package table.eat.now.notification.application.schedule;

import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import table.eat.now.notification.application.event.NotificationEventPublisher;
import table.eat.now.notification.application.event.produce.NotificationScheduleSendEvent;
import table.eat.now.notification.domain.entity.Notification;
import table.eat.now.notification.domain.repository.NotificationRepository;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 23.
 */
@RequiredArgsConstructor
@Service
public class NotificationDelayQueueScheduler {

  private final NotificationRepository notificationRepository;
  private final NotificationEventPublisher publisher;

  @Scheduled(fixedDelay = 1000)
  @Transactional
  public void pollAndDispatch() {
    //100은 초당 처리를 100으로 설정한 부분입니다.별도로 상수로 빼거나 할 예정은 있긴 합니다.
    List<String> notificationIds = notificationRepository.popDueNotifications(100);

    List<Notification> notifications = notificationRepository
        .findByNotificationUuidIn(notificationIds);

    for (Notification notification : notifications) {
      publisher.publish(NotificationScheduleSendEvent.from(notification));
    }
  }

}
