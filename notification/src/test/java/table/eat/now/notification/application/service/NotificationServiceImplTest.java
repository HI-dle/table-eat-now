package table.eat.now.notification.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.RequestBodyUriSpec;
import org.springframework.web.reactive.function.client.WebClient.RequestHeadersSpec;
import org.springframework.web.reactive.function.client.WebClient.ResponseSpec;
import reactor.core.publisher.Mono;
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
import table.eat.now.notification.application.strategy.NotificationFormatterStrategy;
import table.eat.now.notification.application.strategy.NotificationFormatterStrategySelector;
import table.eat.now.notification.application.strategy.NotificationParamExtractor;
import table.eat.now.notification.application.strategy.NotificationTemplate;
import table.eat.now.notification.application.strategy.message.formatter.CompletionFormatter;
import table.eat.now.notification.application.strategy.message.formatter.ConfirmCustomerFormatter;
import table.eat.now.notification.application.strategy.message.formatter.ConfirmOwnerFormatter;
import table.eat.now.notification.application.strategy.message.formatter.ConfirmWaitingFormatter;
import table.eat.now.notification.application.strategy.message.formatter.InfoWaitingFormatter;
import table.eat.now.notification.application.strategy.message.formatter.NoShowFormatter;
import table.eat.now.notification.application.strategy.message.formatter.Reminder1HrFormatter;
import table.eat.now.notification.application.strategy.message.formatter.Reminder9AmFormatter;
import table.eat.now.notification.application.strategy.send.NotificationSenderStrategy;
import table.eat.now.notification.application.strategy.send.NotificationSenderStrategySelector;
import table.eat.now.notification.domain.entity.Notification;
import table.eat.now.notification.domain.entity.NotificationMethod;
import table.eat.now.notification.domain.entity.NotificationStatus;
import table.eat.now.notification.domain.entity.NotificationType;
import table.eat.now.notification.domain.entity.vo.MessageParam;
import table.eat.now.notification.domain.repository.NotificationRepository;
import table.eat.now.notification.domain.repository.search.NotificationSearchCriteria;
import table.eat.now.notification.domain.repository.search.NotificationSearchCriteriaQuery;
import table.eat.now.notification.domain.repository.search.PaginatedResult;
import table.eat.now.notification.infrastructure.sender.EmailSender;
import table.eat.now.notification.infrastructure.sender.SlackSender;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 08.
 */
@ExtendWith(MockitoExtension.class)
class NotificationServiceImplTest {

  @Mock
  private NotificationRepository notificationRepository;

  @Mock
  private WebClient webClient;

  @Mock
  private NotificationFormatterStrategySelector formatterSelector;

  @Mock
  private NotificationSenderStrategySelector sendSelector;

  private final NotificationParamExtractor extractor = new NotificationParamExtractor();

  @Mock
  private NotificationParamExtractor paramExtractor;

  @Mock
  private JavaMailSender mailSender;

  @InjectMocks
  private NotificationServiceImpl notificationService;

  @DisplayName("알림 생성 서비스 테스트")
  @Test
  void notification_create_service_test() {
      // given
    CreateNotificationCommand command = new CreateNotificationCommand(
        1L,
        "CONFIRM_OWNER",
        "고객명",
        LocalDateTime.now(),
        "레스토랑명",
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
        "고객명",
        LocalDateTime.now(),
        "레스토랑명",
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
        "고객명",
        LocalDateTime.now(),
        "레스토랑명",
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
        "고객명",
        LocalDateTime.now(),
        "레스토랑명",
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
        "고객명",
        LocalDateTime.now(),
        "레스토랑명",
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
        "고객명",
        LocalDateTime.now(),
        "레스토랑명",
        "PENDING",
        "EMAIL",
        LocalDateTime.now().plusHours(1)
    );

    NotificationSearchCriteriaQuery query2 = new NotificationSearchCriteriaQuery(
        UUID.randomUUID().toString(),
        1L,
        "CONFIRM_OWNER",
        "고객명",
        LocalDateTime.now(),
        "레스토랑명",
        "PENDING",
        "EMAIL",
        LocalDateTime.now().plusHours(2)
    );

    NotificationSearchCriteriaQuery query3 = new NotificationSearchCriteriaQuery(
        UUID.randomUUID().toString(),
        1L,
        "CONFIRM_OWNER",
        "고객명",
        LocalDateTime.now(),
        "레스토랑명",
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
        "고객명",
        LocalDateTime.now(),
        "레스토랑명",
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

  @Test
  @DisplayName("CONFIRM_OWNER 유형과 이메일로 알림을 전송합니다.")
  void notification_send_service_test_type_CONFIRM_OWNER() {
    // given
    String notificationUuid = UUID.randomUUID().toString();


    Notification existingNotification = Notification.of(
        1L,
        NotificationType.CONFIRM_OWNER,
        "한지훈",
        LocalDateTime.now(),
        "11조 레스토랑",
        NotificationStatus.PENDING,
        NotificationMethod.EMAIL,
        LocalDateTime.now().plusHours(1)
    );

    ReflectionTestUtils.setField(existingNotification, "notificationUuid", notificationUuid);


    when(notificationRepository.findByNotificationUuidAndDeletedByIsNull(notificationUuid))
        .thenReturn(Optional.of(existingNotification));


    NotificationFormatterStrategy formatterStrategy = new ConfirmOwnerFormatter();
    assertThat(formatterStrategy.getType()).isEqualTo(NotificationType.CONFIRM_OWNER);
    when(formatterSelector.select(NotificationType.CONFIRM_OWNER)).thenReturn(formatterStrategy);

    NotificationSenderStrategy senderStrategy = new EmailSender(mailSender);
    when(sendSelector.select(NotificationMethod.EMAIL)).thenReturn(senderStrategy);

    doNothing().when(mailSender).send(any(SimpleMailMessage.class));

    // when
    notificationService.sendNotification(notificationUuid);

    // then
    verify(notificationRepository, times(1)).findByNotificationUuidAndDeletedByIsNull(notificationUuid);


    verify(formatterSelector, times(1)).select(NotificationType.CONFIRM_OWNER);
    verify(sendSelector, times(1)).select(NotificationMethod.EMAIL);


    verify(mailSender, times(1)).send(any(SimpleMailMessage.class));


    assertThat(existingNotification.getStatus()).isEqualTo(NotificationStatus.SENT);
  }

  @Test
  @DisplayName("CONFIRM_CUSTOMER 유형과 이메일로 알림을 전송합니다.")
  void notification_send_service_test_type_CONFIRM_CUSTOMER() {
    // given
    String notificationUuid = UUID.randomUUID().toString();


    Notification existingNotification = Notification.of(
        1L,
        NotificationType.CONFIRM_CUSTOMER,
        "한지훈",
        LocalDateTime.now(),
        "11조 레스토랑",
        NotificationStatus.PENDING,
        NotificationMethod.EMAIL,
        LocalDateTime.now().plusHours(1)
    );

    ReflectionTestUtils.setField(existingNotification, "notificationUuid", notificationUuid);


    when(notificationRepository.findByNotificationUuidAndDeletedByIsNull(notificationUuid))
        .thenReturn(Optional.of(existingNotification));


    NotificationFormatterStrategy formatterStrategy = new ConfirmCustomerFormatter();
    assertThat(formatterStrategy.getType()).isEqualTo(NotificationType.CONFIRM_CUSTOMER);
    when(formatterSelector.select(NotificationType.CONFIRM_CUSTOMER)).thenReturn(formatterStrategy);

    NotificationSenderStrategy senderStrategy = new EmailSender(mailSender);
    when(sendSelector.select(NotificationMethod.EMAIL)).thenReturn(senderStrategy);

    doNothing().when(mailSender).send(any(SimpleMailMessage.class));

    // when
    notificationService.sendNotification(notificationUuid);

    // then
    verify(notificationRepository, times(1)).findByNotificationUuidAndDeletedByIsNull(notificationUuid);


    verify(formatterSelector, times(1)).select(NotificationType.CONFIRM_CUSTOMER);
    verify(sendSelector, times(1)).select(NotificationMethod.EMAIL);


    verify(mailSender, times(1)).send(any(SimpleMailMessage.class));


    assertThat(existingNotification.getStatus()).isEqualTo(NotificationStatus.SENT);
  }

  @Test
  @DisplayName("CONFIRM_WAITING 유형과 이메일로 알림을 전송합니다.")
  void notification_send_service_test_type_CONFIRM_WAITING() {
    // given
    String notificationUuid = UUID.randomUUID().toString();


    Notification existingNotification = Notification.of(
        1L,
        NotificationType.CONFIRM_WAITING,
        "한지훈",
        LocalDateTime.now(),
        "11조 레스토랑",
        NotificationStatus.PENDING,
        NotificationMethod.EMAIL,
        LocalDateTime.now().plusHours(1)
    );

    ReflectionTestUtils.setField(existingNotification, "notificationUuid", notificationUuid);


    when(notificationRepository.findByNotificationUuidAndDeletedByIsNull(notificationUuid))
        .thenReturn(Optional.of(existingNotification));


    NotificationFormatterStrategy formatterStrategy = new ConfirmWaitingFormatter();
    assertThat(formatterStrategy.getType()).isEqualTo(NotificationType.CONFIRM_WAITING);
    when(formatterSelector.select(NotificationType.CONFIRM_WAITING)).thenReturn(formatterStrategy);

    NotificationSenderStrategy senderStrategy = new EmailSender(mailSender);
    when(sendSelector.select(NotificationMethod.EMAIL)).thenReturn(senderStrategy);

    doNothing().when(mailSender).send(any(SimpleMailMessage.class));

    // when
    notificationService.sendNotification(notificationUuid);

    // then
    verify(notificationRepository, times(1)).findByNotificationUuidAndDeletedByIsNull(notificationUuid);


    verify(formatterSelector, times(1)).select(NotificationType.CONFIRM_WAITING);
    verify(sendSelector, times(1)).select(NotificationMethod.EMAIL);


    verify(mailSender, times(1)).send(any(SimpleMailMessage.class));


    assertThat(existingNotification.getStatus()).isEqualTo(NotificationStatus.SENT);
  }

  @Test
  @DisplayName("INFO_WAITING 유형과 이메일로 알림을 전송합니다.")
  void notification_send_service_test_type_INFO_WAITING() {
    // given
    String notificationUuid = UUID.randomUUID().toString();


    Notification existingNotification = Notification.of(
        1L,
        NotificationType.INFO_WAITING,
        "한지훈",
        LocalDateTime.now(),
        "11조 레스토랑",
        NotificationStatus.PENDING,
        NotificationMethod.EMAIL,
        LocalDateTime.now().plusHours(1)
    );

    ReflectionTestUtils.setField(existingNotification, "notificationUuid", notificationUuid);


    when(notificationRepository.findByNotificationUuidAndDeletedByIsNull(notificationUuid))
        .thenReturn(Optional.of(existingNotification));


    NotificationFormatterStrategy formatterStrategy = new InfoWaitingFormatter();
    assertThat(formatterStrategy.getType()).isEqualTo(NotificationType.INFO_WAITING);
    when(formatterSelector.select(NotificationType.INFO_WAITING)).thenReturn(formatterStrategy);

    NotificationSenderStrategy senderStrategy = new EmailSender(mailSender);
    when(sendSelector.select(NotificationMethod.EMAIL)).thenReturn(senderStrategy);

    doNothing().when(mailSender).send(any(SimpleMailMessage.class));

    // when
    notificationService.sendNotification(notificationUuid);

    // then
    verify(notificationRepository, times(1)).findByNotificationUuidAndDeletedByIsNull(notificationUuid);


    verify(formatterSelector, times(1)).select(NotificationType.INFO_WAITING);
    verify(sendSelector, times(1)).select(NotificationMethod.EMAIL);


    verify(mailSender, times(1)).send(any(SimpleMailMessage.class));


    assertThat(existingNotification.getStatus()).isEqualTo(NotificationStatus.SENT);
  }
  @Test
  @DisplayName("COMPLETION 유형과 이메일로 알림을 전송합니다.")
  void notification_send_service_test_type_COMPLETION() {
    // given
    String notificationUuid = UUID.randomUUID().toString();


    Notification existingNotification = Notification.of(
        1L,
        NotificationType.COMPLETION,
        "한지훈",
        LocalDateTime.now(),
        "11조 레스토랑",
        NotificationStatus.PENDING,
        NotificationMethod.EMAIL,
        LocalDateTime.now().plusHours(1)
    );

    ReflectionTestUtils.setField(existingNotification, "notificationUuid", notificationUuid);


    when(notificationRepository.findByNotificationUuidAndDeletedByIsNull(notificationUuid))
        .thenReturn(Optional.of(existingNotification));


    NotificationFormatterStrategy formatterStrategy = new CompletionFormatter();
    assertThat(formatterStrategy.getType()).isEqualTo(NotificationType.COMPLETION);
    when(formatterSelector.select(NotificationType.COMPLETION)).thenReturn(formatterStrategy);

    NotificationSenderStrategy senderStrategy = new EmailSender(mailSender);
    when(sendSelector.select(NotificationMethod.EMAIL)).thenReturn(senderStrategy);

    doNothing().when(mailSender).send(any(SimpleMailMessage.class));

    // when
    notificationService.sendNotification(notificationUuid);

    // then
    verify(notificationRepository, times(1)).findByNotificationUuidAndDeletedByIsNull(notificationUuid);


    verify(formatterSelector, times(1)).select(NotificationType.COMPLETION);
    verify(sendSelector, times(1)).select(NotificationMethod.EMAIL);


    verify(mailSender, times(1)).send(any(SimpleMailMessage.class));


    assertThat(existingNotification.getStatus()).isEqualTo(NotificationStatus.SENT);
  }


  @Test
  @DisplayName("NO_SHOW 유형과 이메일로 알림을 전송합니다.")
  void notification_send_service_test_type_NO_SHOW() {
    // given
    String notificationUuid = UUID.randomUUID().toString();


    Notification existingNotification = Notification.of(
        1L,
        NotificationType.NO_SHOW,
        "한지훈",
        LocalDateTime.now(),
        "11조 레스토랑",
        NotificationStatus.PENDING,
        NotificationMethod.EMAIL,
        LocalDateTime.now().plusHours(1)
    );

    ReflectionTestUtils.setField(existingNotification, "notificationUuid", notificationUuid);


    when(notificationRepository.findByNotificationUuidAndDeletedByIsNull(notificationUuid))
        .thenReturn(Optional.of(existingNotification));


    NotificationFormatterStrategy formatterStrategy = new NoShowFormatter();
    assertThat(formatterStrategy.getType()).isEqualTo(NotificationType.NO_SHOW);
    when(formatterSelector.select(NotificationType.NO_SHOW)).thenReturn(formatterStrategy);

    NotificationSenderStrategy senderStrategy = new EmailSender(mailSender);
    when(sendSelector.select(NotificationMethod.EMAIL)).thenReturn(senderStrategy);

    doNothing().when(mailSender).send(any(SimpleMailMessage.class));

    // when
    notificationService.sendNotification(notificationUuid);

    // then
    verify(notificationRepository, times(1)).findByNotificationUuidAndDeletedByIsNull(notificationUuid);


    verify(formatterSelector, times(1)).select(NotificationType.NO_SHOW);
    verify(sendSelector, times(1)).select(NotificationMethod.EMAIL);


    verify(mailSender, times(1)).send(any(SimpleMailMessage.class));


    assertThat(existingNotification.getStatus()).isEqualTo(NotificationStatus.SENT);
  }

  @Test
  @DisplayName("REMINDER_1HR 유형과 이메일로 알림을 전송합니다.")
  void notification_send_service_test_type_REMINDER_1HR() {
    // given
    String notificationUuid = UUID.randomUUID().toString();


    Notification existingNotification = Notification.of(
        1L,
        NotificationType.REMINDER_1HR,
        "한지훈",
        LocalDateTime.now(),
        "11조 레스토랑",
        NotificationStatus.PENDING,
        NotificationMethod.EMAIL,
        LocalDateTime.now().plusHours(1)
    );

    ReflectionTestUtils.setField(existingNotification, "notificationUuid", notificationUuid);


    when(notificationRepository.findByNotificationUuidAndDeletedByIsNull(notificationUuid))
        .thenReturn(Optional.of(existingNotification));


    NotificationFormatterStrategy formatterStrategy = new Reminder1HrFormatter();
    assertThat(formatterStrategy.getType()).isEqualTo(NotificationType.REMINDER_1HR);
    when(formatterSelector.select(NotificationType.REMINDER_1HR)).thenReturn(formatterStrategy);

    NotificationSenderStrategy senderStrategy = new EmailSender(mailSender);
    when(sendSelector.select(NotificationMethod.EMAIL)).thenReturn(senderStrategy);

    doNothing().when(mailSender).send(any(SimpleMailMessage.class));

    // when
    notificationService.sendNotification(notificationUuid);

    // then
    verify(notificationRepository, times(1)).findByNotificationUuidAndDeletedByIsNull(notificationUuid);


    verify(formatterSelector, times(1)).select(NotificationType.REMINDER_1HR);
    verify(sendSelector, times(1)).select(NotificationMethod.EMAIL);


    verify(mailSender, times(1)).send(any(SimpleMailMessage.class));


    assertThat(existingNotification.getStatus()).isEqualTo(NotificationStatus.SENT);
  }

  @Test
  @DisplayName("REMINDER_9AM 유형과 이메일로 알림을 전송합니다.")
  void notification_send_service_test_type_REMINDER_9AM() {
    // given
    String notificationUuid = UUID.randomUUID().toString();


    Notification existingNotification = Notification.of(
        1L,
        NotificationType.REMINDER_9AM,
        "한지훈",
        LocalDateTime.now(),
        "11조 레스토랑",
        NotificationStatus.PENDING,
        NotificationMethod.EMAIL,
        LocalDateTime.now().plusHours(1)
    );

    ReflectionTestUtils.setField(existingNotification, "notificationUuid", notificationUuid);


    when(notificationRepository.findByNotificationUuidAndDeletedByIsNull(notificationUuid))
        .thenReturn(Optional.of(existingNotification));


    NotificationFormatterStrategy formatterStrategy = new Reminder9AmFormatter();
    assertThat(formatterStrategy.getType()).isEqualTo(NotificationType.REMINDER_9AM);
    when(formatterSelector.select(NotificationType.REMINDER_9AM)).thenReturn(formatterStrategy);

    NotificationSenderStrategy senderStrategy = new EmailSender(mailSender);
    when(sendSelector.select(NotificationMethod.EMAIL)).thenReturn(senderStrategy);

    doNothing().when(mailSender).send(any(SimpleMailMessage.class));

    // when
    notificationService.sendNotification(notificationUuid);

    // then
    verify(notificationRepository, times(1)).findByNotificationUuidAndDeletedByIsNull(notificationUuid);


    verify(formatterSelector, times(1)).select(NotificationType.REMINDER_9AM);
    verify(sendSelector, times(1)).select(NotificationMethod.EMAIL);


    verify(mailSender, times(1)).send(any(SimpleMailMessage.class));


    assertThat(existingNotification.getStatus()).isEqualTo(NotificationStatus.SENT);
  }



  @Test
  @DisplayName("SlackSender 알림 전송 테스트")
  void slackSenderSendTest() {
    // given
    String webhookUrl = "http://slack-webhook-url";
    NotificationTemplate template = new NotificationTemplate("테스트 제목", "테스트 본문");

    SlackSender slackSender = new SlackSender(webClient);
    ReflectionTestUtils.setField(slackSender, "webhookUrl", webhookUrl);

    RequestBodyUriSpec requestBodyUriSpecMock = mock(RequestBodyUriSpec.class);
    when(webClient.post()).thenReturn(requestBodyUriSpecMock);
    when(requestBodyUriSpecMock.uri(webhookUrl)).thenReturn(requestBodyUriSpecMock);

    @SuppressWarnings("unchecked")
    RequestHeadersSpec<?> requestHeadersSpecMock = (RequestHeadersSpec<?>) mock(RequestHeadersSpec.class);
    when(requestBodyUriSpecMock.bodyValue(any())).thenReturn((RequestHeadersSpec) requestHeadersSpecMock);

    ResponseSpec responseSpecMock = mock(ResponseSpec.class);
    when(requestHeadersSpecMock.retrieve()).thenReturn(responseSpecMock);
    when(responseSpecMock.bodyToMono(String.class)).thenReturn(Mono.empty());

    // when
    slackSender.send(1L, template);

    // then
    verify(webClient, times(1)).post();
    verify(requestBodyUriSpecMock, times(1)).uri(webhookUrl);
    verify(requestBodyUriSpecMock, times(1)).bodyValue(any());
    verify(requestHeadersSpecMock, times(1)).retrieve();

}


  @Test
  @DisplayName("SlackSender 예외 발생 시 handleSlackError 호출 확인")
  void slackSenderSendTest_withErrorHandlerCalled() throws InterruptedException {
    // given
    String webhookUrl = "http://slack-webhook-url";
    NotificationTemplate template = new NotificationTemplate("제목", "본문");

    SlackSender slackSender = spy(new SlackSender(webClient));
    ReflectionTestUtils.setField(slackSender, "webhookUrl", webhookUrl);

    RequestBodyUriSpec requestBodyUriSpecMock = mock(RequestBodyUriSpec.class);
    when(webClient.post()).thenReturn(requestBodyUriSpecMock);
    when(requestBodyUriSpecMock.uri(webhookUrl)).thenReturn(requestBodyUriSpecMock);

    @SuppressWarnings("unchecked")
    RequestHeadersSpec<?> requestHeadersSpecMock = (RequestHeadersSpec<?>) mock(RequestHeadersSpec.class);
    when(requestBodyUriSpecMock.bodyValue(any())).thenReturn((RequestHeadersSpec)requestHeadersSpecMock);

    ResponseSpec responseSpecMock = mock(ResponseSpec.class);
    when(requestHeadersSpecMock.retrieve()).thenReturn(responseSpecMock);
    when(responseSpecMock.bodyToMono(String.class)).thenReturn(Mono.error(new RuntimeException("Slack 실패!")));

    // when
    slackSender.send(1L, template);
    Thread.sleep(100);

    // then
    verify(slackSender, times(1)).slackError(any());
  }


  @Test
  @DisplayName("등록된 NotificationMethod 전략을 반환한다")
  void select_valid_strategy_returns_strategy() {
    // given
    NotificationSenderStrategy emailStrategy = mock(NotificationSenderStrategy.class);
    when(emailStrategy.getMethod()).thenReturn(NotificationMethod.EMAIL);

    List<NotificationSenderStrategy> strategies = List.of(emailStrategy);
    NotificationSenderStrategySelector selector = new NotificationSenderStrategySelector(strategies);

    // when
    NotificationSenderStrategy selectedStrategy = selector.select(NotificationMethod.EMAIL);

    // then
    assertThat(selectedStrategy).isEqualTo(emailStrategy);
  }

  @Test
  @DisplayName("등록되지 않은 NotificationMethod는 예외를 던진다")
  void select_invalid_strategy_throws_exception() {
    // given
    NotificationSenderStrategy emailStrategy = mock(NotificationSenderStrategy.class);
    when(emailStrategy.getMethod()).thenReturn(NotificationMethod.EMAIL);

    List<NotificationSenderStrategy> strategies = List.of(emailStrategy);
    NotificationSenderStrategySelector selector = new NotificationSenderStrategySelector(strategies);

    // when & then
    assertThatThrownBy(() -> selector.select(NotificationMethod.SLACK))
        .isInstanceOf(CustomException.class)
        .hasMessage("존재하지 않는 알림 전송 유형 입니다.");
  }



  @Test
  @DisplayName("MessageParam을 통해 Notification에서 파라미터를 추출하여 Map으로 반환한다.")
  void extract_shouldReturnCorrectMap() {
    // given
    MessageParam messageParam = Mockito.mock(MessageParam.class);
    Mockito.when(messageParam.getCustomerName()).thenReturn("한지훈");
    Mockito.when(messageParam.getReservationTime()).thenReturn(LocalDateTime.of(2025, 4, 15, 9, 0));
    Mockito.when(messageParam.getRestaurantName()).thenReturn("11조 레스토랑");

    Notification notification = Mockito.mock(Notification.class);
    Mockito.when(notification.getMessageParam()).thenReturn(messageParam);

    // when
    Map<String, String> result = extractor.extract(notification);

    // then
    assertThat(result).containsEntry("customerName", "한지훈");
    assertThat(result).containsEntry("reservationTime", "2025-04-15 09:00");
    assertThat(result).containsEntry("restaurantName", "11조 레스토랑");
  }

  @Test
  @DisplayName("예약 시간이 null일 경우 빈 문자열을 반환한다.")
  void extract_shouldReturnEmptyReservationTimeWhenNull() {
    // given
    MessageParam messageParam = Mockito.mock(MessageParam.class);
    Mockito.when(messageParam.getCustomerName()).thenReturn("한지훈");
    Mockito.when(messageParam.getReservationTime()).thenReturn(null);
    Mockito.when(messageParam.getRestaurantName()).thenReturn("11조 레스토랑");

    Notification notification = Mockito.mock(Notification.class);
    Mockito.when(notification.getMessageParam()).thenReturn(messageParam);

    // when
    Map<String, String> result = extractor.extract(notification);

    // then
    assertThat(result).containsEntry("customerName", "한지훈");
    assertThat(result).containsEntry("reservationTime", "");
    assertThat(result).containsEntry("restaurantName", "11조 레스토랑");
  }

  @Test
  @DisplayName("유효한 NotificationType에 대해 적절한 전략을 반환한다.")
  void select_shouldReturnCorrectStrategy() {
    // given
    NotificationFormatterStrategy strategyMock = mock(NotificationFormatterStrategy.class);
    NotificationType type = NotificationType.REMINDER_9AM;
    when(strategyMock.getType()).thenReturn(type);

    NotificationFormatterStrategySelector selector = new NotificationFormatterStrategySelector(List.of(strategyMock));
    selector.init();

    // when
    NotificationFormatterStrategy result = selector.select(type);

    // then
    assertThat(result).isEqualTo(strategyMock);
    verify(strategyMock, times(1)).getType();
  }

  @Test
  @DisplayName("유효하지 않은 NotificationType에 대해 예외를 던진다.")
  void select_shouldThrowExceptionWhenInvalidType() {
    // given
    NotificationFormatterStrategy strategyMock = mock(NotificationFormatterStrategy.class);
    when(strategyMock.getType()).thenReturn(NotificationType.REMINDER_9AM);

    NotificationFormatterStrategySelector selector = new NotificationFormatterStrategySelector(List.of(strategyMock));
    selector.init();

    // when & then
    CustomException exception = assertThrows(CustomException.class, () -> selector.select(NotificationType.CONFIRM_CUSTOMER));
    assertThat(exception.getMessage()).isEqualTo("존재하지 않는 알림 타입 입니다.");
  }

}