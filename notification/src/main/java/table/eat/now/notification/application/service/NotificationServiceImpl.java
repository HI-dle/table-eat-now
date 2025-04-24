package table.eat.now.notification.application.service;


import io.micrometer.core.instrument.Timer;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import table.eat.now.notification.application.event.produce.NotificationScheduleSendEvent;
import table.eat.now.notification.application.event.produce.NotificationSendEvent;
import table.eat.now.notification.application.exception.NotificationErrorCode;
import table.eat.now.notification.application.metric.NotificationMetrics;
import table.eat.now.notification.application.strategy.NotificationFormatterStrategy;
import table.eat.now.notification.application.strategy.NotificationFormatterStrategySelector;
import table.eat.now.notification.application.strategy.NotificationParamExtractor;
import table.eat.now.notification.application.strategy.NotificationTemplate;
import table.eat.now.notification.application.strategy.send.NotificationSenderStrategy;
import table.eat.now.notification.application.strategy.send.NotificationSenderStrategySelector;
import table.eat.now.notification.domain.entity.Notification;
import table.eat.now.notification.domain.entity.NotificationMethod;
import table.eat.now.notification.domain.entity.NotificationStatus;
import table.eat.now.notification.domain.entity.NotificationType;
import table.eat.now.notification.domain.repository.NotificationRepository;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 08.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService{

  private final NotificationRepository notificationRepository;
  private final NotificationFormatterStrategySelector formatterSelector;
  private final NotificationSenderStrategySelector sendSelector;
  private final NotificationParamExtractor paramExtractor;
  private final NotificationMetrics metrics;


  //부하 테스트를 위해서 생성 메서드에서 레디스 큐에 넣습니다.
  //차후 여러 서비스에서 알림 처리를 받게 될 때 수정하겠습니다.
  @Override
  @Transactional
  public CreateNotificationInfo createNotification(CreateNotificationCommand command) {
    metrics.incrementCreate();

    CreateNotificationInfo createNotificationInfo = CreateNotificationInfo
        .from(notificationRepository.save(command.toEntity()));

    notificationRepository.addToDelayQueue(
        createNotificationInfo.notificationUuid(), createNotificationInfo.scheduledTime());

    return createNotificationInfo;
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


  @Transactional
  public void sendNotification(String notificationUuid) {
    Notification notification = findByNotification(notificationUuid);
    Timer.Sample sample = metrics.startSendTimer();

    try {
      NotificationFormatterStrategy strategy = formatterSelector.select(notification.getNotificationType());
      Map<String, String> params = paramExtractor.extract(notification);
      NotificationTemplate formattedMessage = strategy.format(params);
      NotificationSenderStrategy senderStrategy = sendSelector.select(notification.getNotificationMethod());

      senderStrategy.send(notification.getUserId(), formattedMessage);
      notification.modifyNotificationStatusIsSent();
      metrics.incrementSendSuccess();
      metrics.recordSendLatency(sample, "manual");

    } catch (Exception e) {
      metrics.incrementSendFail();
      notification.modifyNotificationStatusIsFailed();
      throw CustomException.from(NotificationErrorCode.NOTIFICATION_SEND_FAIL);
    }
  }


  @Override
  @Transactional
  public void consumerNotification(NotificationSendEvent notificationSendEvent) {
    if (notificationSendEvent.payload().scheduledTime() != null) {
      hasScheduleTimeToSaveNotification(notificationSendEvent);
    } else {

      Notification notification = Notification.of(
          notificationSendEvent.userInfoDto().userId(),
          NotificationType.valueOf(notificationSendEvent.payload().notificationType()),
          notificationSendEvent.payload().customerName(),
          notificationSendEvent.payload().reservationTime(),
          notificationSendEvent.payload().restaurantName(),
          NotificationStatus.PENDING,
          NotificationMethod.valueOf(notificationSendEvent.payload().notificationMethod()),
          null
      );

      NotificationFormatterStrategy strategy =
          formatterSelector.select(notification.getNotificationType());

      Map<String, String> params = paramExtractor.extract(notification);

      NotificationTemplate formattedMessage = strategy.format(params);

      NotificationSenderStrategy senderStrategy = sendSelector.select(
          notification.getNotificationMethod());

      senderStrategy.send(notification.getUserId(), formattedMessage);

      notification.modifyNotificationStatusIsSent();
    }
  }

  @Override
  @Transactional
  public void consumerScheduleSendNotification(NotificationScheduleSendEvent event) {
    Timer.Sample sample = metrics.startSendTimer();

    try {
      NotificationFormatterStrategy strategy =
          formatterSelector.select(NotificationType.valueOf(event.payload().notificationType()));

      Map<String, String> params = paramExtractor.extractEvent(event);

      NotificationTemplate formattedMessage = strategy.format(params);

      NotificationSenderStrategy senderStrategy = sendSelector.select(
          NotificationMethod.valueOf(event.payload().notificationMethod()));

      senderStrategy.send(event.payload().userId(), formattedMessage);

      Notification notification = findByNotification(event.payload().notificationUuid());

      notification.modifyNotificationStatusIsSent();
      log.info("수정 완료 :{} ", notification.getStatus());

      metrics.incrementSendSuccess();
      metrics.recordSendLatency(sample, "scheduled");

    } catch (Exception e) {
      log.error("알림 발송 실패: {}", event.payload().notificationUuid(), e);
      metrics.recordSendLatency(sample, "scheduled");
      metrics.incrementSendFail();
    }
  }

  private void hasScheduleTimeToSaveNotification(NotificationSendEvent notificationSendEvent) {
    notificationRepository.save(Notification.of(
        notificationSendEvent.userInfoDto().userId(),
        NotificationType.valueOf(notificationSendEvent.payload().notificationType()),
        notificationSendEvent.payload().customerName(),
        notificationSendEvent.payload().reservationTime(),
        notificationSendEvent.payload().restaurantName(),
        NotificationStatus.PENDING,
        NotificationMethod.valueOf(notificationSendEvent.payload().notificationMethod()),
        notificationSendEvent.payload().scheduledTime()
    ));
  }



  private Notification findByNotification(String notificationUuid) {
    return notificationRepository.findByNotificationUuidAndDeletedByIsNull(notificationUuid)
        .orElseThrow(() ->
            CustomException.from(NotificationErrorCode.INVALID_NOTIFICATION_UUID));
  }

}
