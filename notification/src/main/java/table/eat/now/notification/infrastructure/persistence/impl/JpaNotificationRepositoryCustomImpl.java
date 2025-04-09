package table.eat.now.notification.infrastructure.persistence.impl;

import static table.eat.now.notification.domain.entity.QNotification.notification;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import table.eat.now.notification.domain.entity.Notification;
import table.eat.now.notification.domain.entity.NotificationMethod;
import table.eat.now.notification.domain.entity.NotificationStatus;
import table.eat.now.notification.domain.entity.NotificationType;
import table.eat.now.notification.domain.repository.search.NotificationSearchCriteria;
import table.eat.now.notification.domain.repository.search.NotificationSearchCriteriaQuery;
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
  public PaginatedResult<NotificationSearchCriteriaQuery> searchNotification(
      NotificationSearchCriteria searchCriteria) {

    BooleanExpression[] conditions = new BooleanExpression[] {
        equalNotificationType(searchCriteria.notificationType()),
        equalStatus(searchCriteria.status()),
        equalNotificationMethod(searchCriteria.notificationMethod()),
        containsMessage(searchCriteria.message()),
        equalUserId(searchCriteria.userId())
    };

    List<Notification> fetch = queryFactory.selectFrom(notification)
        .where(conditions)
        .orderBy(getOrderSpecifier(searchCriteria))
        .offset(searchCriteria.page())
        .limit(searchCriteria.size())
        .fetch();

    Long totalCount = queryFactory.select(notification.count())
        .from(notification)
        .where(conditions)
        .fetchOne();

    if (totalCount == null) totalCount = 0L;

    List<NotificationSearchCriteriaQuery> criteriaResponses = fetch.stream()
        .map(NotificationSearchCriteriaQuery::from)
        .toList();
    int totalPages = (int) Math.ceil((double) totalCount / searchCriteria.size());

    return new PaginatedResult<>(
        criteriaResponses,
        searchCriteria.page(),
        searchCriteria.size(),
        totalCount,
        totalPages);
  }

  private BooleanExpression equalNotificationType(String notificationType) {
    return notificationType == null ? null : notification.notificationType.eq(
        NotificationType.valueOf(notificationType));
  }
  private BooleanExpression equalStatus(String status) {
    return status == null ? null : notification.status.eq(
        NotificationStatus.valueOf(status));
  }
  private BooleanExpression equalNotificationMethod(String notificationMethod) {
    return notificationMethod == null ? null : notification.notificationMethod.eq(
        NotificationMethod.valueOf(notificationMethod));
  }
  private BooleanExpression containsMessage(String message) {
    return message == null ? null : notification.message.containsIgnoreCase(message);
  }
  private BooleanExpression equalUserId(Long userId) {
    return userId == null ? null : notification.userId.eq(userId);
  }


  private OrderSpecifier<?> getOrderSpecifier(NotificationSearchCriteria searchCriteria) {
    return "updatedAt".equals(searchCriteria.sortBy()) ? (
        searchCriteria.isAsc() ? notification.updatedAt.asc() : notification.updatedAt.desc())
        : (searchCriteria.isAsc() ? notification.createdAt.asc() : notification.createdAt.desc());
  }
}
