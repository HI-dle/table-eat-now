package table.eat.now.notification.presentation.dto.response;

import java.time.LocalDateTime;
import lombok.Builder;
import table.eat.now.notification.application.dto.response.CreateNotificationInfo;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 08.
 */
@Builder
public record CreateNotificationResponse(String notificationUuid,
                                         Long userId,
                                         String notificationType,
                                         String customerName,
                                         LocalDateTime reservationTime,
                                         String restaurantName,
                                         String status,
                                         String notificationMethod,
                                         LocalDateTime scheduledTime) {
  public static CreateNotificationResponse from(CreateNotificationInfo info) {
    return CreateNotificationResponse.builder()
        .notificationUuid(info.notificationUuid())
        .userId(info.userId())
        .notificationType(info.notificationType())
        .customerName(info.customerName())
        .reservationTime(info.reservationTime())
        .restaurantName(info.restaurantName())
        .status(info.status())
        .notificationMethod(info.notificationMethod())
        .scheduledTime(info.scheduledTime())
        .build();
  }

}
