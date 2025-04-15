package table.eat.now.notification.application.dto.request;

import table.eat.now.notification.domain.repository.search.NotificationSearchCriteria;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 09.
 */
public record NotificationSearchCommand(Long userId,
                                        String notificationType,
                                        String status,
                                        String notificationMethod,
                                        Boolean isAsc,
                                        String sortBy,
                                        int page,
                                        int size) {
  public NotificationSearchCriteria toEntity() {
    return new NotificationSearchCriteria(
        userId,
        notificationType,
        status,
        notificationMethod,
        isAsc,
        sortBy,
        page,
        size
    );
  }

}
