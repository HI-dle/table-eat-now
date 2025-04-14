package table.eat.now.notification.application.dto.response;

import java.time.LocalDateTime;
import lombok.Builder;
import table.eat.now.notification.domain.repository.search.NotificationSearchCriteriaQuery;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 09.
 */
@Builder
public record NotificationSearchInfo(String notificationUuid,
                                     Long userId,
                                     String notificationType,
                                     String customerName,
                                     LocalDateTime reservationTime,
                                     String restaurantName,
                                     String status,
                                     String notificationMethod,
                                     LocalDateTime scheduledTime) {
  public static NotificationSearchInfo from(NotificationSearchCriteriaQuery criteriaResponse) {
    return NotificationSearchInfo.builder()
        .notificationUuid(criteriaResponse.notificationUuid())
        .userId(criteriaResponse.userId())
        .notificationType(criteriaResponse.notificationType())
        .customerName(criteriaResponse.customerName())
        .reservationTime(criteriaResponse.reservationTime())
        .restaurantName(criteriaResponse.restaurantName())
        .status(criteriaResponse.status())
        .notificationMethod(criteriaResponse.notificationMethod())
        .scheduledTime(criteriaResponse.scheduledTime())
        .build();
  }

}
