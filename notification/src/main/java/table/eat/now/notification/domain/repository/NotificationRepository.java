package table.eat.now.notification.domain.repository;


import java.util.Optional;
import java.util.UUID;
import table.eat.now.notification.domain.entity.Notification;
import table.eat.now.notification.domain.repository.search.NotificationSearchCriteria;
import table.eat.now.notification.domain.repository.search.NotificationSearchCriteriaResponse;
import table.eat.now.notification.domain.repository.search.PaginatedResult;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 08.
 */
public interface NotificationRepository {

  Notification save(Notification notification);

  Optional<Notification> findByNotificationUuid(UUID notificationUuid);

  PaginatedResult<NotificationSearchCriteriaResponse> searchNotification(NotificationSearchCriteria searchCriteria);
}
