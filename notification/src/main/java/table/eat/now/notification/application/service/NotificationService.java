package table.eat.now.notification.application.service;

import java.util.UUID;
import table.eat.now.notification.application.dto.request.CreateNotificationCommand;
import table.eat.now.notification.application.dto.request.UpdateNotificationCommand;
import table.eat.now.notification.application.dto.response.CreateNotificationInfo;
import table.eat.now.notification.application.dto.response.UpdateNotificationInfo;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 08.
 */
public interface NotificationService {

  CreateNotificationInfo createNotification(CreateNotificationCommand command);

  UpdateNotificationInfo updateNotification(UpdateNotificationCommand application, UUID notificationUuid);
}
