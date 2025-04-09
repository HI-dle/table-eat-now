package table.eat.now.notification.presentation;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static table.eat.now.common.constant.UserInfoConstant.USER_ID_HEADER;
import static table.eat.now.common.constant.UserInfoConstant.USER_ROLE_HEADER;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.ws.rs.core.MediaType;
import java.time.LocalDateTime;
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
import table.eat.now.notification.application.dto.request.UpdateNotificationCommand;
import table.eat.now.notification.application.dto.response.CreateNotificationInfo;
import table.eat.now.notification.application.dto.response.UpdateNotificationInfo;
import table.eat.now.notification.application.service.NotificationService;
import table.eat.now.notification.domain.entity.Notification;
import table.eat.now.notification.presentation.dto.request.CreateNotificationRequest;
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
    UUID notificationUuid = UUID.randomUUID();

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


}