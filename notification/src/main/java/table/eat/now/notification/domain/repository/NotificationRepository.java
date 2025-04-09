package table.eat.now.notification.domain.repository;


import java.util.Optional;
import java.util.UUID;

import table.eat.now.notification.domain.entity.Notification;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 08.
 */
public interface NotificationRepository {

  Notification save(Notification notification);

  Optional<Notification> findByNotificationUuid(UUID notificationUuid);

}
