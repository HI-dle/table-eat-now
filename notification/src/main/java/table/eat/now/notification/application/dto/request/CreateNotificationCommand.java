package table.eat.now.notification.application.dto.request;

import java.time.LocalDateTime;
import table.eat.now.notification.domain.entity.Notification;
import table.eat.now.notification.domain.entity.NotificationMethod;
import table.eat.now.notification.domain.entity.NotificationStatus;
import table.eat.now.notification.domain.entity.NotificationType;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 08.
 */

public record CreateNotificationCommand(Long userId,
                                        String notificationType,
                                        String message,
                                        String status,
                                        String notificationMethod,
                                        LocalDateTime scheduledTime) {

  public Notification toEntity() {
    return Notification.of(
        userId,
        NotificationType.valueOf(notificationType),
        message,
        NotificationStatus.valueOf(status),
        NotificationMethod.valueOf(notificationMethod),
        scheduledTime
    );
  }

}
