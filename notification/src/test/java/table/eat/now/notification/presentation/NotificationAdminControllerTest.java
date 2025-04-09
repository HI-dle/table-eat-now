package table.eat.now.notification.presentation;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static table.eat.now.common.constant.UserInfoConstant.USER_ID_HEADER;
import static table.eat.now.common.constant.UserInfoConstant.USER_ROLE_HEADER;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.ws.rs.core.MediaType;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import table.eat.now.notification.application.dto.PaginatedResultCommand;
import table.eat.now.notification.application.dto.request.NotificationSearchCommand;
import table.eat.now.notification.application.dto.request.UpdateNotificationCommand;
import table.eat.now.notification.application.dto.response.CreateNotificationInfo;
import table.eat.now.notification.application.dto.response.GetNotificationInfo;
import table.eat.now.notification.application.dto.response.NotificationSearchInfo;
import table.eat.now.notification.application.dto.response.UpdateNotificationInfo;
import table.eat.now.notification.application.service.NotificationService;
import table.eat.now.notification.domain.entity.Notification;
import table.eat.now.notification.presentation.dto.request.CreateNotificationRequest;
import table.eat.now.notification.presentation.dto.request.NotificationSearchCondition;
import table.eat.now.notification.presentation.dto.request.UpdateNotificationRequest;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 08.
 */
@AutoConfigureMockMvc
@WebMvcTest(NotificationAdminController.class)
@ActiveProfiles("test")
class NotificationAdminControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockitoBean
  private NotificationService notificationService;

  @DisplayName("알림 생성 테스트")
  @Test
  void notification_create_test() throws Exception {
      // given
    CreateNotificationRequest request = new CreateNotificationRequest(
        1L,
        "CONFIRM_OWNER",
        "예약이 확정되었습니다.",
        "PENDING",
        "SLACK",
        LocalDateTime.now().plusHours(1)
    );

    Notification entity = request.toApplication().toEntity();

    given(notificationService.createNotification(any()))
        .willReturn(CreateNotificationInfo.from(entity));



    // when
    ResultActions resultActions = mockMvc.perform(post("/admin/v1/notifications")
        .header("Authorization", "Bearer {ACCESS_TOKEN}")
        .header(USER_ID_HEADER, "1")
        .header(USER_ROLE_HEADER, "MASTER")
        .content(objectMapper.writeValueAsString(request))
        .contentType(MediaType.APPLICATION_JSON));

      // then
    resultActions.andExpect(status().isCreated())
        .andExpect(header().string("Location", "/admin/v1/notifications"))
        .andDo(print());
  }

  @DisplayName("알림 수정 테스트")
  @Test
  void notification_update_test() throws Exception {
    // given
    String notificationUuid = UUID.randomUUID().toString();

    UpdateNotificationRequest request = new UpdateNotificationRequest(
        1L,
        "CONFIRM_OWNER",
        "예약이 확정되었습니다.",
        "SENT",
        "EMAIL",
        LocalDateTime.now().plusHours(2)
    );

    UpdateNotificationInfo info = UpdateNotificationInfo.builder()
        .notificationUuid(notificationUuid)
        .userId(request.userId())
        .notificationType(request.notificationType())
        .message(request.message())
        .status(request.status())
        .notificationMethod(request.notificationMethod())
        .scheduledTime(request.scheduledTime())
        .build();

    given(notificationService.updateNotification(
        any(UpdateNotificationCommand.class), eq(notificationUuid)))
        .willReturn(info);

    // when
    ResultActions resultActions = mockMvc.perform(put("/admin/v1/notifications/{notificationsUuid}", notificationUuid)
        .header("Authorization", "Bearer {ACCESS_TOKEN}")
        .header(USER_ID_HEADER, "1")
        .header(USER_ROLE_HEADER, "MASTER")
        .content(objectMapper.writeValueAsString(request))
        .contentType(MediaType.APPLICATION_JSON));

    // then
    resultActions.andExpect(status().isOk())
        .andDo(print());
  }
  @DisplayName("알림 단건 조회 테스트")
  @Test
  void find_one_notification_test() throws Exception {
    // given
    String notificationUuid = UUID.randomUUID().toString();

    GetNotificationInfo info = GetNotificationInfo.builder()
        .notificationUuid(notificationUuid)
        .userId(1L)
        .notificationType("CONFIRM_OWNER")
        .message("예약이 확정되었습니다.")
        .status("SENT")
        .notificationMethod("EMAIL")
        .scheduledTime(LocalDateTime.now().plusHours(2))
        .build();

    given(notificationService.findNotification(notificationUuid))
        .willReturn(info);

    // when
    ResultActions resultActions = mockMvc.perform(get(
        "/admin/v1/notifications/{notificationsUuid}", notificationUuid)
        .header("Authorization", "Bearer {ACCESS_TOKEN}")
        .header(USER_ID_HEADER, "1")
        .header(USER_ROLE_HEADER, "MASTER")
        .contentType(MediaType.APPLICATION_JSON));

    // then
    resultActions.andExpect(status().isOk())
        .andDo(print());
  }

  @DisplayName("알림 페이징 검색 컨트롤러 테스트")
  @Test
  void search_notifications_test() throws Exception {
    // given
    NotificationSearchCondition condition = new NotificationSearchCondition(
        1L,
        "REMINDER_9AM",
        "테스트 메시지",
        "PENDING",
        "SLACK",
        true,
        "scheduledTime",
        0,
        2
    );
    NotificationSearchCommand command = condition.toApplication();

    NotificationSearchInfo info1 = new NotificationSearchInfo(
        UUID.randomUUID().toString(),
        condition.userId(),
        condition.notificationType(),
        condition.message(),
        condition.status(),
        condition.notificationMethod(),
        LocalDateTime.now().plusHours(1)
    );
    NotificationSearchInfo info2 = new NotificationSearchInfo(
        UUID.randomUUID().toString(),
        condition.userId(),
        condition.notificationType(),
        condition.message(),
        condition.status(),
        condition.notificationMethod(),
        LocalDateTime.now().plusHours(2)
    );

    var serviceResult = new PaginatedResultCommand<>(
        List.of(info1, info2),
        condition.page(),
        condition.size(),
        2L,
        1
    );

    given(notificationService.searchNotification(eq(command)))
        .willReturn(serviceResult);

    // when / then
    mockMvc.perform(get("/admin/v1/notifications")
            .param("userId", condition.userId().toString())
            .param("notificationType", condition.notificationType())
            .param("message", condition.message())
            .param("status", condition.status())
            .param("notificationMethod", condition.notificationMethod())
            .param("isAsc", condition.isAsc().toString())
            .param("sortBy", condition.sortBy())
            .param("page", String.valueOf(condition.page()))
            .param("size", String.valueOf(condition.size()))
            .header("Authorization", "Bearer {ACCESS_TOKEN}")
            .header(USER_ID_HEADER, "1")
            .header(USER_ROLE_HEADER, "MASTER")
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content", org.hamcrest.Matchers.hasSize(2)))
        .andExpect(jsonPath("$.page").value(condition.page()))
        .andExpect(jsonPath("$.size").value(condition.size()))
        .andExpect(jsonPath("$.totalElements").value(2))
        .andExpect(jsonPath("$.totalPages").value(1))
        .andExpect(jsonPath("$.content[0].message").value(condition.message()))
        .andExpect(jsonPath("$.content[1].message").value(condition.message()))
        .andDo(print());
  }




}