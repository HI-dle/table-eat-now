package table.eat.now.waiting.waiting_request.presentation;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static table.eat.now.common.constant.UserInfoConstant.USER_ID_HEADER;
import static table.eat.now.common.constant.UserInfoConstant.USER_ROLE_HEADER;

import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import table.eat.now.common.resolver.dto.CurrentUserInfoDto;
import table.eat.now.common.resolver.dto.UserRole;
import table.eat.now.waiting.helper.ControllerTestSupport;
import table.eat.now.waiting.waiting_request.application.usecase.dto.command.UpdateWaitingRequestStatusAdminCommand;
import table.eat.now.waiting.waiting_request.application.usecase.dto.command.WaitingRequestEntranceAdminCommand;
import table.eat.now.waiting.waiting_request.application.usecase.dto.query.GetCurrentWaitingRequestsAdminQuery;
import table.eat.now.waiting.waiting_request.application.usecase.dto.query.GetWaitingRequestAdminQuery;
import table.eat.now.waiting.waiting_request.application.usecase.dto.response.PageResult;
import table.eat.now.waiting.waiting_request.fixture.GetWaitingRequestInfoFixture;

class WaitingRequestAdminControllerTest extends ControllerTestSupport {

  @DisplayName("대기 요청 입장 처리 요청 - 200 성공 응답")
  @Test
  void processWaitingRequestEntrance() throws Exception {
    // given
    var waitingRequestUuid = UUID.randomUUID().toString();
    var userInfo = CurrentUserInfoDto.of(2L, UserRole.STAFF);
    var command = WaitingRequestEntranceAdminCommand.of(
        userInfo.userId(), userInfo.role(), waitingRequestUuid);

    given(router.execute(eq(command))).willReturn(null);

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
    var info = GetWaitingRequestInfoFixture.create(
        2, UUID.randomUUID().toString(), UUID.randomUUID().toString(), "WAITING");
    var query = GetWaitingRequestAdminQuery.of(
        userInfo.userId(), userInfo.role(), info.waitingRequestUuid());

    given(router.execute(eq(query))).willReturn(info);

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
  void getCurrentWaitingRequestsAdmin() throws Exception {

    // given
    var userInfo = CurrentUserInfoDto.of(2L, UserRole.STAFF);
    var pageable = PageRequest.of(0, 10);
    var requests = GetWaitingRequestInfoFixture.createList(0, 10);
    var dailyWaitingUuid = requests.get(0).dailyWaitingUuid();

    var query = GetCurrentWaitingRequestsAdminQuery.of(
        userInfo.userId(), userInfo.role(), dailyWaitingUuid,
        pageable.getPageNumber(), pageable.getPageSize(), pageable.getOffset());
    var page = PageResult.of(requests, 100, 10, 1, 10);

    given(router.execute(eq(query))).willReturn(page);

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

  @DisplayName("STAFF 사용자의 대기 상태 변경 요청 검증 - 200 응답")
  @Test
  void postponeWaitingRequest() throws Exception {
    // given
    var userInfo = CurrentUserInfoDto.of(2L, UserRole.STAFF);
    var waitingRequestUuid = UUID.randomUUID().toString();
    var type = "SEATED";

    var command = UpdateWaitingRequestStatusAdminCommand.of(
        userInfo.userId(), userInfo.role(), waitingRequestUuid, type);

    given(router.execute(eq(command))).willReturn(null);

    // when
    ResultActions resultActions = mockMvc.perform(
        patch("/admin/v1/waiting-requests/{waitingRequestUuid}/status", waitingRequestUuid)
            .param("type", type)
            .header("Authorization", "Bearer {ACCESS_TOKEN}")
            .header(USER_ID_HEADER, userInfo.userId())
            .header(USER_ROLE_HEADER, userInfo.role()));

    // then
    resultActions.andExpect(status().isOk())
        .andDo(print());
  }
}