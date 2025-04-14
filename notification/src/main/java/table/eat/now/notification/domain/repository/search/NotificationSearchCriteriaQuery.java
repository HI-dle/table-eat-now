package table.eat.now.notification.domain.repository.search;

import java.time.LocalDateTime;
import lombok.Builder;
import table.eat.now.notification.domain.entity.Notification;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 09.
 */
@Builder
public record NotificationSearchCriteriaQuery(String notificationUuid,
                                              Long userId,
                                              String notificationType,
                                              String customerName,
                                              LocalDateTime reservationTime,
                                              String restaurantName,
                                              String status,
                                              String notificationMethod,
                                              LocalDateTime scheduledTime) {

  public static NotificationSearchCriteriaQuery from(Notification notification) {
    return NotificationSearchCriteriaQuery.builder()
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
