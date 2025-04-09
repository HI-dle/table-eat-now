package table.eat.now.notification.infrastructure.persistence;

import table.eat.now.notification.domain.repository.search.NotificationSearchCriteria;
import table.eat.now.notification.domain.repository.search.NotificationSearchCriteriaQuery;
import table.eat.now.notification.domain.repository.search.PaginatedResult;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 09.
 */
public interface JpaNotificationRepositoryCustom {
  PaginatedResult<NotificationSearchCriteriaQuery> searchNotification(
      NotificationSearchCriteria searchCriteria);

}
