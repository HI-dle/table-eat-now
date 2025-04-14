package table.eat.now.notification.application.Strategy.send;

import table.eat.now.notification.application.Strategy.NotificationTemplate;
import table.eat.now.notification.domain.entity.NotificationMethod;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 15.
 */
public interface NotificationSenderStrategy {
  NotificationMethod getMethod();
  void send(Long userId, NotificationTemplate template);
}

