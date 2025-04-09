package table.eat.now.notification.application.dto.response;

import java.time.LocalDateTime;
import lombok.Builder;
import table.eat.now.notification.domain.entity.Notification;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 08.
 */
@Builder
public record UpdateNotificationInfo(String notificationUuid,
                                     Long userId,
                                     String notificationType,
                                     String message,
                                     String status,
                                     String notificationMethod,
                                     LocalDateTime scheduledTime) {

  public static UpdateNotificationInfo from(Notification notification) {
    return UpdateNotificationInfo.builder()
        .notificationUuid(notification.getNotificationUuid())
        .userId(notification.getUserId())
        .notificationType(notification.getNotificationType().toString())
        .message(notification.getMessage())
        .status(notification.getStatus().toString())
        .notificationMethod(notification.getNotificationMethod().toString())
        .scheduledTime(notification.getScheduledTime())
        .build();
  }

}
