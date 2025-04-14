package table.eat.now.waiting.waiting_request.presentation;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static table.eat.now.common.constant.UserInfoConstant.USER_ID_HEADER;
import static table.eat.now.common.constant.UserInfoConstant.USER_ROLE_HEADER;

import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.ResultActions;
import table.eat.now.common.resolver.dto.CurrentUserInfoDto;
import table.eat.now.common.resolver.dto.UserRole;
import table.eat.now.waiting.helper.ControllerTestSupport;
import table.eat.now.waiting.waiting_request.application.dto.response.GetWaitingRequestInfo;
import table.eat.now.waiting.waiting_request.application.service.WaitingRequestService;

@WebMvcTest(WaitingRequestAdminController.class)
class WaitingRequestAdminControllerTest extends ControllerTestSupport {

  @MockitoBean
  private WaitingRequestService waitingRequestService;

  @DisplayName("대기 요청 입장 처리 요청 - 200 성공 응답")
  @Test
  void processWaitingRequestEntrance() throws Exception {
    // given
    var waitingRequestUuid = UUID.randomUUID().toString();
    var userInfo = CurrentUserInfoDto.of(2L, UserRole.STAFF);

    doNothing().when(waitingRequestService)
        .processWaitingRequestEntrance(userInfo, waitingRequestUuid);

    // when
    ResultActions resultActions = mockMvc.perform(
        post("/admin/v1/waiting-requests/{waitingRequestUuid}/entrance", waitingRequestUuid)
        .header("Authorization", "Bearer {ACCESS_TOKEN}")
        .header(USER_ID_HEADER, userInfo.userId())
        .header(USER_ROLE_HEADER, userInfo.role())
        .contentType(MediaType.APPLICATION_JSON));

    // then
    resultActions.andExpect(status().isOk())
        .andDo(print());
  }

  @DisplayName("STAFF 사용자의 대기 요청 조회 검증 - 200 응답")
  @Test
  void getWaitingRequest() throws Exception {
    // given
    var userInfo = CurrentUserInfoDto.of(2L, UserRole.STAFF);
    var info = GetWaitingRequestInfo.builder()
        .waitingRequestUuid(UUID.randomUUID().toString())
        .dailyWaitingUuid(UUID.randomUUID().toString())
        .restaurantUuid(UUID.randomUUID().toString())
        .restaurantName("혜주네 식당")
        .phone("01000000000")
        .slackId("slackId@example.com")
        .seatSize(3)
        .sequence(10)
        .rank(2L)
        .estimatedWaitingMin(21L)
        .build();

    given(waitingRequestService.getWaitingRequestAdmin(
        eq(userInfo), eq(info.waitingRequestUuid())))
        .willReturn(info);

    // when
    ResultActions resultActions = mockMvc.perform(
        get("/admin/v1/waiting-requests/{waitingRequestUuid}", info.waitingRequestUuid())
            .header("Authorization", "Bearer {ACCESS_TOKEN}")
            .header(USER_ID_HEADER, userInfo.userId())
            .header(USER_ROLE_HEADER, userInfo.role()));

    // then
    resultActions.andExpect(status().isOk())
        .andExpect(jsonPath("$.waitingRequestUuid").value(info.waitingRequestUuid()))
        .andExpect(jsonPath("$.dailyWaitingUuid").value(info.dailyWaitingUuid()))
        .andExpect(jsonPath("$.restaurantUuid").value(info.restaurantUuid()))
        .andExpect(jsonPath("$.restaurantName").value(info.restaurantName()))
        .andDo(print());
  }
}