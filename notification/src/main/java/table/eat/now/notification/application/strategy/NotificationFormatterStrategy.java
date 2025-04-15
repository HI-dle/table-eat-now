package table.eat.now.notification.application.strategy;

import java.util.Map;
import table.eat.now.notification.domain.entity.NotificationType;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 14.
 */
public interface NotificationFormatterStrategy {
  NotificationType getType();
  NotificationTemplate format(Map<String, String> params);
}
