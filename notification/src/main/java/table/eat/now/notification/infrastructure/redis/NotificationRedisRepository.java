package table.eat.now.notification.infrastructure.redis;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 23.
 */
public interface NotificationRedisRepository {

  void addToDelayQueue(String notificationUuId, LocalDateTime scheduledTime);
  List<String> popDueNotifications(int maxCount);
}
