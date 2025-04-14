package table.eat.now.waiting.waiting_request.presentation;

import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static table.eat.now.common.constant.UserInfoConstant.USER_ID_HEADER;
import static table.eat.now.common.constant.UserInfoConstant.USER_ROLE_HEADER;

import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.ResultActions;
import table.eat.now.common.aop.AuthCheckAspect;
import table.eat.now.common.config.WebConfig;
import table.eat.now.common.exception.GlobalErrorHandler;
import table.eat.now.common.resolver.CurrentUserInfoResolver;
import table.eat.now.common.resolver.CustomPageableArgumentResolver;
import table.eat.now.common.resolver.dto.CurrentUserInfoDto;
import table.eat.now.common.resolver.dto.UserRole;
import table.eat.now.waiting.helper.ControllerTestSupport;
import table.eat.now.waiting.waiting_request.application.service.WaitingRequestService;
import table.eat.now.waiting.waiting_request.presentation.dto.request.EnterWaitingRequestRequest;

@Import({
    WebConfig.class,
    CustomPageableArgumentResolver.class,
    CurrentUserInfoResolver.class,
    GlobalErrorHandler.class,
    AuthCheckAspect.class
})
@EnableAspectJAutoProxy
@WebMvcTest(WaitingRequestAdminController.class)
class WaitingRequestAdminControllerTest extends ControllerTestSupport {

  @MockitoBean
  private WaitingRequestService waitingRequestService;

  @DisplayName("대기 요청 입장 처리 요청 - 200 성공 응답")
  @Test
  void processWaitingRequestEntrance() throws Exception {
    // given
    var waitingRequestUuid = UUID.randomUUID().toString();
    CurrentUserInfoDto userInfo = CurrentUserInfoDto.of(2L, UserRole.STAFF);
    var request = EnterWaitingRequestRequest.builder()
        .restaurantUuid(UUID.randomUUID())
        .dailyWaitingUuid(UUID.randomUUID())
        .build();

    doNothing().when(waitingRequestService)
        .processWaitingRequestEntrance(userInfo, waitingRequestUuid, request.toCommand());

    // when
    ResultActions resultActions = mockMvc.perform(
        post("/admin/v1/waiting-requests/{waitingRequestUuid}/entrance", waitingRequestUuid)
        .header("Authorization", "Bearer {ACCESS_TOKEN}")
        .header(USER_ID_HEADER, userInfo.userId())
        .header(USER_ROLE_HEADER, userInfo.role())
        .content(objectMapper.writeValueAsString(request))
        .contentType(MediaType.APPLICATION_JSON));

    // then
    resultActions.andExpect(status().isOk())
        .andDo(print());
  }
}