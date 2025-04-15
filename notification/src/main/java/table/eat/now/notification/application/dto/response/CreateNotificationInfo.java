package table.eat.now.notification.application.dto.response;

import java.time.LocalDateTime;
import lombok.Builder;
import table.eat.now.notification.domain.entity.Notification;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 08.
 */
@Builder
public record CreateNotificationInfo(String notificationUuid,
                                     Long userId,
                                     String notificationType,
                                     String customerName,
                                     LocalDateTime reservationTime,
                                     String restaurantName,
                                     String status,
                                     String notificationMethod,
                                     LocalDateTime scheduledTime) {

  public static CreateNotificationInfo from(Notification notification) {
    return CreateNotificationInfo.builder()
        .notificationUuid(notification.getNotificationUuid())
        .userId(notification.getUserId())
        .notificationType(notification.getNotificationType().toString())
        .customerName(notification.getMessageParam().getCustomerName())
        .reservationTime(notification.getMessageParam().getReservationTime())
        .restaurantName(notification.getMessageParam().getRestaurantName())
        .status(notification.getStatus().toString())
        .notificationMethod(notification.getNotificationMethod().toString())
        .scheduledTime(notification.getScheduledTime())
        .build();
  }

}
