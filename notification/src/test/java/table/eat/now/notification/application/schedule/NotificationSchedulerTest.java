//package table.eat.now.notification.application.schedule;
//
//
//import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.eq;
//import static org.mockito.BDDMockito.given;
//import static org.mockito.Mockito.doThrow;
//import static org.mockito.Mockito.verify;
//
//import java.time.LocalDateTime;
//import java.util.List;
//import java.util.Map;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import table.eat.now.notification.application.metric.NotificationMetrics;
//import table.eat.now.notification.application.strategy.NotificationFormatterStrategy;
//import table.eat.now.notification.application.strategy.NotificationFormatterStrategySelector;
//import table.eat.now.notification.application.strategy.NotificationParamExtractor;
//import table.eat.now.notification.application.strategy.NotificationTemplate;
//import table.eat.now.notification.application.strategy.send.NotificationSenderStrategy;
//import table.eat.now.notification.application.strategy.send.NotificationSenderStrategySelector;
//import table.eat.now.notification.domain.entity.Notification;
//import table.eat.now.notification.domain.entity.NotificationMethod;
//import table.eat.now.notification.domain.entity.NotificationStatus;
//import table.eat.now.notification.domain.entity.NotificationType;
//import table.eat.now.notification.domain.repository.NotificationRepository;
//
///**
// * @author : hanjihoon
// * @Date : 2025. 04. 16.
// */
//@ExtendWith(MockitoExtension.class)
//class NotificationSchedulerTest {
//
//  @Mock
//  private NotificationRepository notificationRepository;
//
//  @Mock
//  private NotificationFormatterStrategySelector formatterSelector;
//
//  @Mock
//  private NotificationSenderStrategySelector sendSelector;
//
//  @Mock
//  private NotificationParamExtractor paramExtractor;
//
//  @Mock
//  private NotificationFormatterStrategy formatterStrategy;
//
//  @Mock
//  private NotificationSenderStrategy senderStrategy;
//
//  @Mock
//  private NotificationMetrics meterRegistry;
//
//  private NotificationScheduler scheduler;
//
//  @BeforeEach
//  void setUp() {
//    scheduler = new NotificationScheduler(
//        notificationRepository,
//        formatterSelector,
//        sendSelector,
//        paramExtractor,
//        meterRegistry
//    );
//  }
//
//  @DisplayName("스케줄러가 정상적으로 알림을 전송한다")
//  @Test
//  void scheduler_success_notification_sent() {
//    // given
//    Notification notification = Notification.of(
//        1L,
//        NotificationType.CONFIRM_CUSTOMER,
//        "11조",
//        LocalDateTime.of(2025, 4, 20, 18, 30),
//        "HI-dle 식당",
//        NotificationStatus.PENDING,
//        NotificationMethod.EMAIL,
//        LocalDateTime.now().minusMinutes(5)
//    );
//
//    given(notificationRepository.findByStatusAndScheduledTimeLessThanEqualAndDeletedByIsNull(
//        any(), any())).willReturn(List.of(notification));
//
//    given(formatterSelector.select(any())).willReturn(formatterStrategy);
//    given(paramExtractor.extract(any())).willReturn(Map.of(
//        "customerName", "11조",
//        "reservationTime", "2025-04-20 18:30",
//        "restaurantName", "HI-dle 식당"
//    ));
//    given(formatterStrategy.format(any())).willReturn(new NotificationTemplate("제목", "본문"));
//
//    given(sendSelector.select(any())).willReturn(senderStrategy);
//
//    // when
//    scheduler.sendScheduledNotifications();
//
//    // then
//    assertThat(notification.getStatus()).isEqualTo(NotificationStatus.SENT);
//    verify(senderStrategy).send(eq(1L), any(NotificationTemplate.class));
//  }
//
//  @DisplayName("스케줄러가 알림 전송에 실패하면 상태는 FAILED가 된다")
//  @Test
//  void scheduler_failed_notification_no_sent() {
//    // given
//    Notification notification = Notification.of(
//        1L,
//        NotificationType.CONFIRM_CUSTOMER,
//        "11조",
//        LocalDateTime.of(2025, 4, 20, 18, 30),
//        "HI-dle 식당",
//        NotificationStatus.PENDING,
//        NotificationMethod.EMAIL,
//        LocalDateTime.now().minusMinutes(5)
//    );
//
//    given(notificationRepository.findByStatusAndScheduledTimeLessThanEqualAndDeletedByIsNull(
//        any(), any())).willReturn(List.of(notification));
//
//    given(formatterSelector.select(any())).willReturn(formatterStrategy);
//    given(paramExtractor.extract(any())).willReturn(Map.of(
//        "customerName", "11조",
//        "reservationTime", "2025-04-20 18:30",
//        "restaurantName", "HI-dle 식당"
//    ));
//    given(formatterStrategy.format(any())).willReturn(new NotificationTemplate("제목", "본문"));
//    given(sendSelector.select(any())).willReturn(senderStrategy);
//
//
//    doThrow(new RuntimeException("전송 실패")).when(senderStrategy).send(eq(1L), any(NotificationTemplate.class));
//
//    // when
//    scheduler.sendScheduledNotifications();
//
//    // then
//    assertThat(notification.getStatus()).isEqualTo(NotificationStatus.FAILED);
//  }
//
//}