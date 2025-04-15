package table.eat.now.notification.application.strategy.send;

import table.eat.now.notification.application.strategy.NotificationTemplate;
import table.eat.now.notification.domain.entity.NotificationMethod;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 15.
 */
public interface NotificationSenderStrategy {
  NotificationMethod getMethod();
  void send(Long userId, NotificationTemplate template);
}

