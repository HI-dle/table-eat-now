package table.eat.now.notification.application.event.produce;

import java.time.LocalDateTime;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 18.
 */
public record NotificationSendPayload(String notificationType,
                                      String customerName,
                                      LocalDateTime reservationTime,
                                      String restaurantName,
                                      String notificationMethod,
                                      LocalDateTime scheduledTime) {

}
