package table.eat.now.notification.infrastructure;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import table.eat.now.notification.domain.entity.Notification;
import table.eat.now.notification.domain.entity.NotificationStatus;
import table.eat.now.notification.domain.repository.NotificationRepository;
import table.eat.now.notification.domain.repository.search.NotificationSearchCriteria;
import table.eat.now.notification.domain.repository.search.NotificationSearchCriteriaQuery;
import table.eat.now.notification.domain.repository.search.PaginatedResult;
import table.eat.now.notification.infrastructure.persistence.JpaNotificationRepository;
import table.eat.now.notification.infrastructure.redis.NotificationRedisRepository;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 23.
 */
@Repository
@RequiredArgsConstructor
public class NotificationRepositoryImpl implements NotificationRepository {

  private final NotificationRedisRepository notificationRedisRepository;
  private final JpaNotificationRepository jpaNotificationRepository;

  @Override
  public Notification save(Notification notification) {
    return jpaNotificationRepository.save(notification);
  }

  @Override
  public Optional<Notification> findByNotificationUuidAndDeletedByIsNull(String notificationUuid) {
    return jpaNotificationRepository.findByNotificationUuidAndDeletedByIsNull(notificationUuid);
  }

  @Override
  public PaginatedResult<NotificationSearchCriteriaQuery> searchNotification(
      NotificationSearchCriteria searchCriteria) {
    return jpaNotificationRepository.searchNotification(searchCriteria);
  }

  @Override
  public List<Notification> findByStatusAndScheduledTimeLessThanEqualAndDeletedByIsNull(
      NotificationStatus status, LocalDateTime localDateTime) {
    return null;
  }
}
