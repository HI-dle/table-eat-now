package table.eat.now.notification.infrastructure.persistence;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import table.eat.now.notification.domain.entity.Notification;
import table.eat.now.notification.domain.entity.NotificationStatus;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 08.
 */
public interface JpaNotificationRepository extends
    JpaRepository<Notification, Long>, JpaNotificationRepositoryCustom {
  Optional<Notification> findByNotificationUuidAndDeletedByIsNull(String notificationUuid);

  List<Notification> findByNotificationUuidIn(List<String> notificationIds);


  List<Notification> findByStatusAndScheduledTimeLessThanEqualAndDeletedByIsNull(
      NotificationStatus status, LocalDateTime localDateTime);
}

