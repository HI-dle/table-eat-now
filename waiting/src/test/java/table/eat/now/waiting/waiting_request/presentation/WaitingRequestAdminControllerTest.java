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
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.ResultActions;
import table.eat.now.common.resolver.dto.CurrentUserInfoDto;
import table.eat.now.common.resolver.dto.UserRole;
import table.eat.now.waiting.helper.ControllerTestSupport;
import table.eat.now.waiting.waiting_request.application.dto.response.PageResult;
import table.eat.now.waiting.waiting_request.application.service.WaitingRequestService;
import table.eat.now.waiting.waiting_request.fixture.GetWaitingRequestInfoFixture;

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
    var info = GetWaitingRequestInfoFixture.create(2, UUID.randomUUID().toString(), UUID.randomUUID().toString());

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

  @DisplayName("STAFF 사용자의 대기 요청 목록 조회 검증 - 200 응답")
  @Test
  void getWaitingRequests() throws Exception {

    // given
    var userInfo = CurrentUserInfoDto.of(2L, UserRole.STAFF);
    var pageable = PageRequest.of(0, 10);
    var requests = GetWaitingRequestInfoFixture.createList(0, 10);
    var dailyWaitingUuid = requests.get(0).dailyWaitingUuid();
    var page = PageResult.of(requests, 100, 10, 1, 10);
    given(waitingRequestService.getWaitingRequestsAdmin(
        eq(userInfo), eq(dailyWaitingUuid), eq(pageable)))
        .willReturn(page);

    // when
    ResultActions resultActions = mockMvc.perform(
        get("/admin/v1/waiting-requests")
            .param("dailyWaitingUuid", dailyWaitingUuid)
            .header("Authorization", "Bearer {ACCESS_TOKEN}")
            .header(USER_ID_HEADER, userInfo.userId())
            .header(USER_ROLE_HEADER, userInfo.role()));

    // then
    resultActions.andExpect(status().isOk())
        .andExpect(jsonPath("$.totalElements").value(page.totalElements()))
        .andExpect(jsonPath("$.pageNumber").value(page.pageNumber()))
        .andExpect(jsonPath("$.waitingRequests[0].dailyWaitingUuid").value(dailyWaitingUuid))
        .andExpect(jsonPath("$.waitingRequests.size()").value(10))
        .andDo(print());
  }
}