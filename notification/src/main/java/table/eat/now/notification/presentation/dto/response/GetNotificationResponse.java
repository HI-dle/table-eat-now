package table.eat.now.notification.presentation.dto.response;

import java.time.LocalDateTime;
import lombok.Builder;
import table.eat.now.notification.application.dto.response.GetNotificationInfo;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 09.
 */
@Builder
public record GetNotificationResponse(String notificationUuid,
                                      Long userId,
                                      String notificationType,
                                      String message,
                                      String status,
                                      String notificationMethod,
                                      LocalDateTime scheduledTime) {

  public static GetNotificationResponse from(GetNotificationInfo getNotificationInfo) {
    return GetNotificationResponse.builder()
        .notificationUuid(getNotificationInfo.notificationUuid())
        .userId(getNotificationInfo.userId())
        .notificationType(getNotificationInfo.notificationType())
        .message(getNotificationInfo.message())
        .status(getNotificationInfo.status())
        .notificationMethod(getNotificationInfo.notificationMethod())
        .scheduledTime(getNotificationInfo.scheduledTime())
        .build();
  }
}
