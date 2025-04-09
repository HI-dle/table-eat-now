package table.eat.now.notification.presentation.dto.response;

import java.time.LocalDateTime;
import lombok.Builder;
import table.eat.now.notification.application.dto.response.NotificationSearchInfo;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 09.
 */
@Builder
public record NotificationSearchResponse(String notificationUuid,
                                         Long userId,
                                         String notificationType,
                                         String message,
                                         String status,
                                         String notificationMethod,
                                         LocalDateTime scheduledTime) {
  
  public static NotificationSearchResponse from(NotificationSearchInfo info) {
    return NotificationSearchResponse.builder()
        .notificationUuid(info.notificationUuid())
        .userId(info.userId())
        .notificationType(info.notificationType())
        .message(info.message())
        .status(info.status())
        .notificationMethod(info.notificationMethod())
        .scheduledTime(info.scheduledTime())
        .build();
  }

}
