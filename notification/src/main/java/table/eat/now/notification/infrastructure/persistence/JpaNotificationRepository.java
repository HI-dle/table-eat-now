package table.eat.now.notification.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import table.eat.now.notification.domain.entity.Notification;
import table.eat.now.notification.domain.repository.NotificationRepository;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 08.
 */
public interface JpaNotificationRepository extends
    JpaRepository<Notification, Long>, NotificationRepository {

}
