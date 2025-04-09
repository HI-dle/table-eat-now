package table.eat.now.notification.application.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import table.eat.now.common.exception.CustomException;
import table.eat.now.notification.application.dto.PaginatedResultCommand;
import table.eat.now.notification.application.dto.request.CreateNotificationCommand;
import table.eat.now.notification.application.dto.request.NotificationSearchCommand;
import table.eat.now.notification.application.dto.request.UpdateNotificationCommand;
import table.eat.now.notification.application.dto.response.CreateNotificationInfo;
import table.eat.now.notification.application.dto.response.GetNotificationInfo;
import table.eat.now.notification.application.dto.response.NotificationSearchInfo;
import table.eat.now.notification.application.dto.response.UpdateNotificationInfo;
import table.eat.now.notification.application.exception.NotificationErrorCode;
import table.eat.now.notification.domain.entity.Notification;
import table.eat.now.notification.domain.repository.NotificationRepository;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 08.
 */
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService{

  private final NotificationRepository notificationRepository;

  @Override
  @Transactional
  public CreateNotificationInfo createNotification(CreateNotificationCommand command) {
    return CreateNotificationInfo
        .from(notificationRepository.save(command.toEntity()));
  }


  @Override
  @Transactional
  public UpdateNotificationInfo updateNotification(UpdateNotificationCommand command,
      String notificationUuid) {
    Notification notification = findByNotification(notificationUuid);

    notification.modifyNotification(
        command.userId(),
        command.notificationType(),
        command.message(),
        command.status(),
        command.notificationMethod(),
        command.scheduledTime()
    );

    return UpdateNotificationInfo.from(notification);
  }

  @Override
  public GetNotificationInfo findNotification(String notificationsUuid) {
    return GetNotificationInfo.from(findByNotification(notificationsUuid));
  }

  @Override
  public PaginatedResultCommand<NotificationSearchInfo> searchNotification(
      NotificationSearchCommand command) {

    return PaginatedResultCommand.from(
        notificationRepository.searchNotification(command.toEntity()));
  }

  private Notification findByNotification(String notificationUuid) {
    return notificationRepository.findByNotificationUuid(notificationUuid)
        .orElseThrow(() ->
            CustomException.from(NotificationErrorCode.INVALID_NOTIFICATION_UUID));
  }

}
