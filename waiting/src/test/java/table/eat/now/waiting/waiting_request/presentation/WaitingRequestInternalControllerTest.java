package table.eat.now.waiting.waiting_request.presentation;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static table.eat.now.common.constant.UserInfoConstant.USER_ID_HEADER;
import static table.eat.now.common.constant.UserInfoConstant.USER_ROLE_HEADER;

import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.ResultActions;
import table.eat.now.common.resolver.dto.CurrentUserInfoDto;
import table.eat.now.common.resolver.dto.UserRole;
import table.eat.now.waiting.helper.ControllerTestSupport;
import table.eat.now.waiting.waiting_request.application.router.UsecaseRouter;
import table.eat.now.waiting.waiting_request.application.usecase.dto.query.GetWaitingRequestInternalQuery;
import table.eat.now.waiting.waiting_request.fixture.GetWaitingRequestInfoFixture;

@WebMvcTest(WaitingRequestInternalController.class)
class WaitingRequestInternalControllerTest extends ControllerTestSupport {

  @MockitoBean
  private UsecaseRouter router;

  @DisplayName("대기 요청 내부 조회 - 200 성공")
  @Test
  void getWaitingRequestInternal() throws Exception {
    // given
    var userInfo = CurrentUserInfoDto.of(2L, UserRole.CUSTOMER);
    var info = GetWaitingRequestInfoFixture.create(
        2, UUID.randomUUID().toString(), UUID.randomUUID().toString(), "SEATED");
    var query = GetWaitingRequestInternalQuery.of(userInfo.userId(), userInfo.role(), info.waitingRequestUuid());

    given(router.execute(eq(query))).willReturn(info);

    // when
    ResultActions resultActions = mockMvc.perform(
        get("/internal/v1/waiting-requests/{waitingRequestUuid}", info.waitingRequestUuid())
            .header("Authorization", "Bearer {ACCESS_TOKEN}")
            .header(USER_ID_HEADER, userInfo.userId())
            .header(USER_ROLE_HEADER, userInfo.role()));

    // then
    resultActions.andExpect(status().isOk())
        .andExpect(jsonPath("$.waitingRequestUuid").value(info.waitingRequestUuid()))
        .andExpect(jsonPath("$.dailyWaitingUuid").value(info.dailyWaitingUuid()))
        .andExpect(jsonPath("$.restaurantUuid").value(info.restaurantUuid()))
        .andExpect(jsonPath("$.restaurantName").value(info.restaurantName()))
        .andExpect(jsonPath("$.status").value(info.status()))
        .andExpect(jsonPath("$.rank").value(info.rank()))
        .andDo(print());
  }
}