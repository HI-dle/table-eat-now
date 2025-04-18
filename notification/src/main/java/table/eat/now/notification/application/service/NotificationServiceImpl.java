package table.eat.now.notification.application.service;


import java.time.format.DateTimeFormatter;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import table.eat.now.common.exception.CustomException;
import table.eat.now.common.resolver.dto.CurrentUserInfoDto;
import table.eat.now.notification.application.dto.PaginatedResultCommand;
import table.eat.now.notification.application.dto.request.CreateNotificationCommand;
import table.eat.now.notification.application.dto.request.NotificationSearchCommand;
import table.eat.now.notification.application.dto.request.UpdateNotificationCommand;
import table.eat.now.notification.application.dto.response.CreateNotificationInfo;
import table.eat.now.notification.application.dto.response.GetNotificationInfo;
import table.eat.now.notification.application.dto.response.NotificationSearchInfo;
import table.eat.now.notification.application.dto.response.UpdateNotificationInfo;
import table.eat.now.notification.application.exception.NotificationErrorCode;
import table.eat.now.notification.application.strategy.NotificationFormatterStrategy;
import table.eat.now.notification.application.strategy.NotificationFormatterStrategySelector;
import table.eat.now.notification.application.strategy.NotificationParamExtractor;
import table.eat.now.notification.application.strategy.NotificationTemplate;
import table.eat.now.notification.application.strategy.send.NotificationSenderStrategy;
import table.eat.now.notification.application.strategy.send.NotificationSenderStrategySelector;
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
  private final NotificationFormatterStrategySelector formatterSelector;
  private final NotificationSenderStrategySelector sendSelector;
  private final NotificationParamExtractor paramExtractor;
  private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");


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
        command.customerName(),
        command.reservationTime(),
        command.restaurantName(),
        command.status(),
        command.notificationMethod(),
        command.scheduledTime()
    );

    return UpdateNotificationInfo.from(notification);
  }

  @Override
  @Transactional(readOnly = true)
  public GetNotificationInfo findNotification(String notificationsUuid) {
    return GetNotificationInfo.from(findByNotification(notificationsUuid));
  }

  @Override
  @Transactional(readOnly = true)
  public PaginatedResultCommand<NotificationSearchInfo> searchNotification(
      NotificationSearchCommand command) {

    return PaginatedResultCommand.from(
        notificationRepository.searchNotification(command.toEntity()));
  }

  @Override
  @Transactional
  public void deleteNotification(String notificationsUuid, CurrentUserInfoDto userInfo) {
    Notification notification = findByNotification(notificationsUuid);
    notification.delete(userInfo.userId());
  }

  @Override
  @Transactional
  public void sendNotification(String notificationUuid) {

    Notification notification = findByNotification(notificationUuid);

    NotificationFormatterStrategy strategy = formatterSelector.select(notification.getNotificationType());
    Map<String, String> params = paramExtractor.extract(notification);
    NotificationTemplate formattedMessage = strategy.format(params);

    NotificationSenderStrategy senderStrategy = sendSelector.select(
        notification.getNotificationMethod());

    senderStrategy.send(notification.getUserId(), formattedMessage);

    notification.modifyNotificationStatusIsSent();
  }





  private Notification findByNotification(String notificationUuid) {
    return notificationRepository.findByNotificationUuidAndDeletedByIsNull(notificationUuid)
        .orElseThrow(() ->
            CustomException.from(NotificationErrorCode.INVALID_NOTIFICATION_UUID));
  }

}
