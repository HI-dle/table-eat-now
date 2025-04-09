package table.eat.now.notification.application.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 09.
 */
public record NotificationSearchInfo(UUID notificationUuid,
                                     Long userId,
                                     String notificationType,
                                     String message,
                                     String status,
                                     String notificationMethod,
                                     LocalDateTime scheduledTime) {

}
