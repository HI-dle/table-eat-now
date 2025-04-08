package table.eat.now.notification.application.service;

import table.eat.now.notification.application.dto.request.CreateNotificationCommand;
import table.eat.now.notification.application.dto.response.CreateNotificationInfo;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 08.
 */
public interface NotificationService {

  CreateNotificationInfo createNotification(CreateNotificationCommand command);
}
