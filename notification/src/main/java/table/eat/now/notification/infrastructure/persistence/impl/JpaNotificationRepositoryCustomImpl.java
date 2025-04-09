package table.eat.now.notification.infrastructure.persistence.impl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import table.eat.now.notification.domain.repository.search.NotificationSearchCriteria;
import table.eat.now.notification.domain.repository.search.NotificationSearchCriteriaResponse;
import table.eat.now.notification.domain.repository.search.PaginatedResult;
import table.eat.now.notification.infrastructure.persistence.JpaNotificationRepositoryCustom;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 09.
 */
@RequiredArgsConstructor
public class JpaNotificationRepositoryCustomImpl implements JpaNotificationRepositoryCustom {

  private final JPAQueryFactory queryFactory;
  @Override
  public PaginatedResult<NotificationSearchCriteriaResponse> searchNotification(
      NotificationSearchCriteria searchCriteria) {
    return null;
  }
}
