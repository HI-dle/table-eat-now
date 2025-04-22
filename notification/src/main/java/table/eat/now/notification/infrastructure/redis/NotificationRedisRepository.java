package table.eat.now.notification.infrastructure.redis;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 23.
 */
public interface NotificationRedisRepository {

  void addToDelayQueue(Long notificationId, LocalDateTime scheduledTime);
  List<Long> popDueNotifications(int maxCount);
}
