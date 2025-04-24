package table.eat.now.notification.application.event.produce;

import java.time.LocalDateTime;
import lombok.Builder;
import table.eat.now.notification.domain.entity.Notification;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 18.
 */
@Builder
public record NotificationScheduleSendPayload(Long userId,
                                              String notificationUuid,
                                              String notificationType,
                                              String customerName,
                                              LocalDateTime reservationTime,
                                              String restaurantName,
                                              String notificationMethod,
                                              LocalDateTime scheduledTime) {

  public static NotificationScheduleSendPayload from(Notification notification) {
    return NotificationScheduleSendPayload.builder()
        .userId(notification.getUserId())
        .notificationUuid(notification.getNotificationUuid())
        .notificationType(notification.getNotificationType().toString())
        .customerName(notification.getMessageParam().getCustomerName())
        .reservationTime(notification.getMessageParam().getReservationTime())
        .restaurantName(notification.getMessageParam().getRestaurantName())
        .notificationMethod(notification.getNotificationMethod().toString())
        .scheduledTime(notification.getScheduledTime())
        .build();
  }
}
