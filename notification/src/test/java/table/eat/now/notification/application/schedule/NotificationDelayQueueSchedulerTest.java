package table.eat.now.notification.application.schedule;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import table.eat.now.notification.application.event.NotificationEventPublisher;
import table.eat.now.notification.application.event.produce.NotificationScheduleSendEvent;
import table.eat.now.notification.application.metric.NotificationMetrics;
import table.eat.now.notification.domain.entity.Notification;
import table.eat.now.notification.domain.entity.NotificationMethod;
import table.eat.now.notification.domain.entity.NotificationStatus;
import table.eat.now.notification.domain.entity.NotificationType;
import table.eat.now.notification.domain.repository.NotificationRepository;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 23.
 */

@ExtendWith(MockitoExtension.class)
class NotificationDelayQueueSchedulerTest {
  @Mock
  private NotificationRepository notificationRepository;

  @Mock
  private NotificationEventPublisher publisher;

  @Mock
  private NotificationMetrics metrics;

  @InjectMocks
  private NotificationDelayQueueScheduler scheduler;



  @Test
  @DisplayName("알림이 발송되면, 메트릭과 이벤트가 적절히 처리된다.")
  void pollAndDispatchTest() throws Exception {
    List<String> notificationIds = List.of("uuid-1", "uuid-2", "uuid-3");
    List<Notification> notifications = List.of(
        Notification.of(
            1L,
            NotificationType.REMINDER_1HR,
            "한지훈",
            LocalDateTime.now(),
            "11조 레스토랑",
            NotificationStatus.PENDING,
            NotificationMethod.EMAIL,
            LocalDateTime.now().plusHours(1)
        ),
        Notification.of(
            2L,
            NotificationType.REMINDER_1HR,
            "한지훈",
            LocalDateTime.now(),
            "11조 레스토랑",
            NotificationStatus.PENDING,
            NotificationMethod.EMAIL,
            LocalDateTime.now().plusHours(1)
        ),
        Notification.of(
            3L,
            NotificationType.REMINDER_1HR,
            "한지훈",
            LocalDateTime.now(),
            "11조 레스토랑",
            NotificationStatus.PENDING,
            NotificationMethod.EMAIL,
            LocalDateTime.now().plusHours(1)
        )
    );

    int testDispatchCount = 1000;
    ReflectionTestUtils.setField(scheduler, "maxDispatchCount", testDispatchCount);

    when(notificationRepository.popDueNotifications(testDispatchCount))
        .thenReturn(notificationIds);
    when(notificationRepository.findByNotificationUuidIn(notificationIds))
        .thenReturn(notifications);

    doAnswer(invocation -> {
      Runnable runnable = invocation.getArgument(0);
      runnable.run();
      return null;
    }).when(metrics).recordSchedulerExecution(any(Runnable.class));

    doNothing().when(metrics).recordFetchedScheduledCount(anyInt());
    doNothing().when(publisher).publish(any());

    scheduler.pollAndDispatch();

    verify(notificationRepository).popDueNotifications(testDispatchCount);
    verify(notificationRepository).findByNotificationUuidIn(notificationIds);
    verify(publisher, times(notifications.size())).publish(any(NotificationScheduleSendEvent.class));
    verify(metrics).recordSchedulerExecution(any());
    verify(metrics).recordFetchedScheduledCount(notifications.size());
  }

  @Test
  @DisplayName("발송할 알림이 없을 때, 메트릭이 기록되지 않는다.")
  void pollAndDispatch_noNotificationsTest() throws Exception {
    List<String> notificationIds = List.of();

    //중복 뺄 수 있지만 귀찮아서..ㅠ봐주세요...
    int testDispatchCount = 1000;
    ReflectionTestUtils.setField(scheduler, "maxDispatchCount", testDispatchCount);

    when(notificationRepository.popDueNotifications(testDispatchCount))
        .thenReturn(notificationIds);

    scheduler.pollAndDispatch();

    verify(notificationRepository).popDueNotifications(testDispatchCount);
    verify(notificationRepository, never()).findByNotificationUuidIn(any());
    verify(publisher, never()).publish(any());
    verify(metrics, never()).recordSchedulerExecution(any());
    verify(metrics, never()).recordFetchedScheduledCount(anyInt());
  }

}
