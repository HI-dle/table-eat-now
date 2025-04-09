package table.eat.now.notification.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import table.eat.now.notification.application.dto.request.CreateNotificationCommand;
import table.eat.now.notification.application.dto.response.CreateNotificationInfo;
import table.eat.now.notification.domain.entity.Notification;
import table.eat.now.notification.domain.repository.NotificationRepository;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 08.
 */
@ExtendWith(MockitoExtension.class)
class NotificationServiceImplTest {

  @Mock
  private NotificationRepository notificationRepository;

  @InjectMocks
  private NotificationServiceImpl notificationService;

  @DisplayName("알림 생성 서비스 테스트")
  @Test
  void notification_create_service_test() {
      // given
    CreateNotificationCommand command = new CreateNotificationCommand(
        1L,
        "CONFIRM_OWNER",
        "예약이 확정되었습니다.",
        "PENDING",
        "SLACK",
        LocalDateTime.now().plusHours(1)
    );

    Notification entity = command.toEntity();

    when(notificationRepository.save(any(Notification.class)))
        .thenReturn(entity);

    // when
    CreateNotificationInfo result = notificationService.createNotification(command);

    // then
    assertThat(result.userId()).isEqualTo(command.userId());
    assertThat(result.message()).isEqualTo(command.message());

    verify(notificationRepository).save(any(Notification.class));
  }

}