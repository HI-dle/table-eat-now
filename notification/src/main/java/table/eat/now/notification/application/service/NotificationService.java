package table.eat.now.notification.application.service;


import table.eat.now.common.resolver.dto.CurrentUserInfoDto;
import table.eat.now.notification.application.dto.PaginatedResultCommand;
import table.eat.now.notification.application.dto.request.CreateNotificationCommand;
import table.eat.now.notification.application.dto.request.NotificationSearchCommand;
import table.eat.now.notification.application.dto.request.UpdateNotificationCommand;
import table.eat.now.notification.application.dto.response.CreateNotificationInfo;
import table.eat.now.notification.application.dto.response.GetNotificationInfo;
import table.eat.now.notification.application.dto.response.NotificationSearchInfo;
import table.eat.now.notification.application.dto.response.UpdateNotificationInfo;


/**
 * @author : hanjihoon
 * @Date : 2025. 04. 08.
 */
public interface NotificationService {

  CreateNotificationInfo createNotification(CreateNotificationCommand command);


  UpdateNotificationInfo updateNotification(UpdateNotificationCommand application, String notificationUuid);

  GetNotificationInfo findNotification(String notificationsUuid);

  PaginatedResultCommand<NotificationSearchInfo> searchNotification(NotificationSearchCommand command);

  void deleteNotification(String notificationsUuid, CurrentUserInfoDto userInfo);
}
