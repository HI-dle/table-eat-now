package table.eat.now.notification.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import table.eat.now.notification.application.dto.request.CreateNotificationCommand;
import table.eat.now.notification.application.dto.response.CreateNotificationInfo;
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
  public CreateNotificationInfo createNotification(CreateNotificationCommand command) {
    return CreateNotificationInfo
        .from(notificationRepository.save(command.toEntity()));
  }
}
