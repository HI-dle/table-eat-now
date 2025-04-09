package table.eat.now.notification.domain.repository.search;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 09.
 */
public record NotificationSearchCriteria(Long userId,
                                         String notificationType,
                                         String message,
                                         String status,
                                         String notificationMethod,
                                         Boolean isAsc,
                                         String sortBy,
                                         int page,
                                         int size) {

}
