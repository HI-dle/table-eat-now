package table.eat.now.notification.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import table.eat.now.common.exception.CustomException;
import table.eat.now.common.resolver.dto.CurrentUserInfoDto;
import table.eat.now.common.resolver.dto.UserRole;
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
import table.eat.now.notification.domain.entity.NotificationMethod;
import table.eat.now.notification.domain.entity.NotificationStatus;
import table.eat.now.notification.domain.entity.NotificationType;
import table.eat.now.notification.domain.repository.NotificationRepository;
import table.eat.now.notification.domain.repository.search.NotificationSearchCriteria;
import table.eat.now.notification.domain.repository.search.NotificationSearchCriteriaQuery;
import table.eat.now.notification.domain.repository.search.PaginatedResult;

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
  @DisplayName("알림 수정 서비스 테스트")
  @Test
  void notification_update_service_test() {
    // given
    String notificationUuid = UUID.randomUUID().toString();

    Notification existingNotification = Notification.of(
        1L,
        NotificationType.CONFIRM_OWNER,
        "기존 메시지",
        NotificationStatus.PENDING,
        NotificationMethod.SLACK,
        LocalDateTime.now().plusHours(1)
    );


    //강제 주입
    ReflectionTestUtils.setField(existingNotification,
        "notificationUuid", notificationUuid);

    UpdateNotificationCommand command = new UpdateNotificationCommand(
        1L,
        "CONFIRM_OWNER",
        "수정된 메시지",
        "SENT",
        "EMAIL",
        LocalDateTime.now().plusHours(2)
    );

    when(notificationRepository.findByNotificationUuidAndDeletedByIsNull(notificationUuid))
        .thenReturn(Optional.of(existingNotification));

    // when
    UpdateNotificationInfo result = notificationService.updateNotification(command, notificationUuid);

    // then
    assertThat(result.notificationUuid()).isEqualTo(notificationUuid);
    assertThat(result.message()).isEqualTo("수정된 메시지");
    assertThat(result.status()).isEqualTo("SENT");
    assertThat(result.notificationMethod()).isEqualTo("EMAIL");

    verify(notificationRepository).findByNotificationUuidAndDeletedByIsNull(notificationUuid);
  }
  @DisplayName("알림 수정 서비스 실패 테스트")
  @Test
  void notification_update_service_fail_test_not_found_uuid() {
    // given
    String invalidUuid = UUID.randomUUID().toString();

    UpdateNotificationCommand command = new UpdateNotificationCommand(
        1L,
        "CONFIRM_OWNER",
        "수정된 메시지",
        "SENT",
        "EMAIL",
        LocalDateTime.now().plusHours(2)
    );

    when(notificationRepository.findByNotificationUuidAndDeletedByIsNull(invalidUuid))
        .thenReturn(Optional.empty());

    // when
    // then
    assertThatThrownBy(() ->
        notificationService.updateNotification(command, invalidUuid))
        .isInstanceOf(CustomException.class)
        .hasMessageContaining(NotificationErrorCode.INVALID_NOTIFICATION_UUID.getMessage());

    verify(notificationRepository).findByNotificationUuidAndDeletedByIsNull(invalidUuid);
  }

  @DisplayName("알림 단건 조회 서비스 테스트")
  @Test
  void notification_find_service_test() {
    // given
    String notificationUuid = UUID.randomUUID().toString();

    Notification existingNotification = Notification.of(
        1L,
        NotificationType.CONFIRM_OWNER,
        "조회 테스트 메시지",
        NotificationStatus.PENDING,
        NotificationMethod.EMAIL,
        LocalDateTime.now().plusHours(1)
    );


    ReflectionTestUtils.setField(existingNotification, "notificationUuid", notificationUuid);

    when(notificationRepository.findByNotificationUuidAndDeletedByIsNull(notificationUuid))
        .thenReturn(Optional.of(existingNotification));

    // when
    GetNotificationInfo result = notificationService.findNotification(notificationUuid);

    // then
    assertThat(result.notificationUuid()).isEqualTo(notificationUuid);
    assertThat(result.userId()).isEqualTo(1L);
    assertThat(result.notificationType()).isEqualTo("CONFIRM_OWNER");
    assertThat(result.message()).isEqualTo("조회 테스트 메시지");
    assertThat(result.status()).isEqualTo("PENDING");
    assertThat(result.notificationMethod()).isEqualTo("EMAIL");
    assertThat(result.scheduledTime()).isEqualTo(existingNotification.getScheduledTime());

    verify(notificationRepository).findByNotificationUuidAndDeletedByIsNull(notificationUuid);
  }

  @DisplayName("알림 페이징 검색 서비스 테스트")
  @Test
  void notification_search_service_test() {
    NotificationSearchCommand command = new NotificationSearchCommand(
        1L,
        "CONFIRM_OWNER",
        "테스트 메시지",
        "PENDING",
        "EMAIL",
        true,
        "scheduledTime",
        0,
        2
    );

    NotificationSearchCriteria criteria = command.toEntity();

    NotificationSearchCriteriaQuery query1 = new NotificationSearchCriteriaQuery(
        UUID.randomUUID().toString(),
        1L,
        "CONFIRM_OWNER",
        "테스트 메시지1",
        "PENDING",
        "EMAIL",
        LocalDateTime.now().plusHours(1)
    );

    NotificationSearchCriteriaQuery query2 = new NotificationSearchCriteriaQuery(
        UUID.randomUUID().toString(),
        1L,
        "CONFIRM_OWNER",
        "테스트 메시지2",
        "PENDING",
        "EMAIL",
        LocalDateTime.now().plusHours(2)
    );

    NotificationSearchCriteriaQuery query3 = new NotificationSearchCriteriaQuery(
        UUID.randomUUID().toString(),
        1L,
        "CONFIRM_OWNER",
        "테스트 메시지3",
        "PENDING",
        "EMAIL",
        LocalDateTime.now().plusHours(3)
    );

    List<NotificationSearchCriteriaQuery> queryList = List.of(query1, query2);

    PaginatedResult<NotificationSearchCriteriaQuery> mockResult = new PaginatedResult<>(
        queryList,
        0,
        2,
        3L,
        2
    );

    when(notificationRepository.searchNotification(criteria)).thenReturn(mockResult);

    PaginatedResultCommand<NotificationSearchInfo> result = notificationService.searchNotification(command);

    assertThat(result.content()).hasSize(2);
    assertThat(result.page()).isEqualTo(0);
    assertThat(result.size()).isEqualTo(2);
    assertThat(result.totalElements()).isEqualTo(3L);
    assertThat(result.totalPages()).isEqualTo(2);
    assertThat(result.content().get(0).message()).isEqualTo("테스트 메시지1");
    assertThat(result.content().get(1).message()).isEqualTo("테스트 메시지2");

    verify(notificationRepository).searchNotification(criteria);
  }

  @DisplayName("알림 삭제 서비스 테스트")
  @Test
  void notification_delete_service_test() {
    // given
    String notificationUuid = UUID.randomUUID().toString();
    Long userId = 1L;

    Notification notification = Notification.of(
        userId,
        NotificationType.CONFIRM_OWNER,
        "삭제 테스트 메시지",
        NotificationStatus.PENDING,
        NotificationMethod.EMAIL,
        LocalDateTime.now().plusHours(1)
    );


    ReflectionTestUtils.setField(notification, "notificationUuid", notificationUuid);

    when(notificationRepository.findByNotificationUuidAndDeletedByIsNull(notificationUuid))
        .thenReturn(Optional.of(notification));

    CurrentUserInfoDto userInfo = new CurrentUserInfoDto(userId, UserRole.MASTER);

    // when
    notificationService.deleteNotification(notificationUuid, userInfo);

    // then
    assertThat(notification.getDeletedBy()).isEqualTo(userId);

    verify(notificationRepository).findByNotificationUuidAndDeletedByIsNull(notificationUuid);
  }




}