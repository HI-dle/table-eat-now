package table.eat.now.notification.domain.repository.search;

import java.time.LocalDateTime;
import java.util.UUID;
import table.eat.now.notification.domain.entity.NotificationMethod;
import table.eat.now.notification.domain.entity.NotificationStatus;
import table.eat.now.notification.domain.entity.NotificationType;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 09.
 */
public record NotificationSearchCriteriaResponse(UUID notificationUuid,
                                                 Long userId,
                                                 NotificationType notificationType,
                                                 String message,
                                                 NotificationStatus status,
                                                 NotificationMethod notificationMethod,
                                                 LocalDateTime scheduledTime) {

}
