package table.eat.now.notification.domain.repository;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import table.eat.now.notification.domain.entity.Notification;
import table.eat.now.notification.domain.entity.NotificationStatus;
import table.eat.now.notification.domain.repository.search.NotificationSearchCriteria;
import table.eat.now.notification.domain.repository.search.NotificationSearchCriteriaQuery;
import table.eat.now.notification.domain.repository.search.PaginatedResult;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 08.
 */
public interface NotificationRepository {

  Notification save(Notification notification);

  Optional<Notification> findByNotificationUuidAndDeletedByIsNull(String notificationUuid);

  PaginatedResult<NotificationSearchCriteriaQuery> searchNotification(NotificationSearchCriteria searchCriteria);

  List<Notification> findByStatusAndScheduledTimeLessThanEqualAndDeletedByIsNull(
      NotificationStatus status, LocalDateTime localDateTime);

  void addToDelayQueue(String notificationUuid, LocalDateTime scheduledTime);
  List<String> popDueNotifications(int maxCount);

  List<Notification> findByNotificationUuidIn(List<String> notificationIds);

}
