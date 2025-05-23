package table.eat.now.notification.application.dto.request;

import java.time.LocalDateTime;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 09.
 */
public record UpdateNotificationCommand(Long userId,
                                        String notificationType,
                                        String customerName,
                                        LocalDateTime reservationTime,
                                        String restaurantName,
                                        String status,
                                        String notificationMethod,
                                        LocalDateTime scheduledTime) {

}
