package table.eat.now.waiting.waiting_request.presentation;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static table.eat.now.common.constant.UserInfoConstant.USER_ID_HEADER;
import static table.eat.now.common.constant.UserInfoConstant.USER_ROLE_HEADER;

import java.util.UUID;
import org.hamcrest.Matchers;
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
import table.eat.now.waiting.waiting_request.presentation.dto.request.CreateWaitingRequestRequest;

@WebMvcTest(WaitingRequestApiController.class)
class WaitingRequestApiControllerTest extends ControllerTestSupport {

  @MockitoBean
  private WaitingRequestService waitingRequestService;

  @DisplayName("대기 요청 생성 검증 - 201 응답")
  @Test
  void createWaitingRequest() throws Exception {
    // given
    var userInfo = CurrentUserInfoDto.of(2L, UserRole.CUSTOMER);
    var request = CreateWaitingRequestRequest.builder()
        .dailyWaitingUuid(UUID.randomUUID())
        .phone("01000000000")
        .slackId("slackId@example.com")
        .seatSize(3)
        .build();
    var waitingRequestUuid = UUID.randomUUID().toString();

    given(waitingRequestService.createWaitingRequest(eq(userInfo), eq(request.toCommand())))
        .willReturn(waitingRequestUuid);

    // when
    ResultActions resultActions = mockMvc.perform(post("/api/v1/waiting-requests")
        .header("Authorization", "Bearer {ACCESS_TOKEN}")
        .header(USER_ID_HEADER, "2")
        .header(USER_ROLE_HEADER, "CUSTOMER")
        .content(objectMapper.writeValueAsString(request))
        .contentType(MediaType.APPLICATION_JSON));

    // then
    resultActions.andExpect(status().isCreated())
        .andExpect(header().string(
            "Location",
            Matchers.endsWith(String.format("/api/v1/waiting-requests/%s", waitingRequestUuid))))
        .andDo(print());
  }

  @DisplayName("대기 요청 조회 검증 - 200 응답")
  @Test
  void getWaitingRequest() throws Exception {
    // given
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

    given(waitingRequestService.getWaitingRequest(
        any(), eq(info.waitingRequestUuid()), eq(info.phone())))
        .willReturn(info);

    // when
    ResultActions resultActions = mockMvc.perform(
        get("/api/v1/waiting-requests/{waitingRequestUuid}", info.waitingRequestUuid())
            .param("phone", info.phone()));

    // then
    resultActions.andExpect(status().isOk())
        .andExpect(jsonPath("$.waitingRequestUuid").value(info.waitingRequestUuid()))
        .andExpect(jsonPath("$.dailyWaitingUuid").value(info.dailyWaitingUuid()))
        .andExpect(jsonPath("$.restaurantUuid").value(info.restaurantUuid()))
        .andExpect(jsonPath("$.restaurantName").value(info.restaurantName()))
        .andDo(print());
  }

  @DisplayName("대기 연기 요청 검증 - 200 응답")
  @Test
  void postponeWaitingRequest() throws Exception {
    // given
    var waitingRequestUuid = UUID.randomUUID().toString();
    var phone = "01000000000";

    doNothing().when(waitingRequestService)
        .postponeWaitingRequest(null, waitingRequestUuid, phone);

    // when
    ResultActions resultActions = mockMvc.perform(
        patch("/api/v1/waiting-requests/{waitingRequestUuid}/postpone", waitingRequestUuid)
            .param("phone", phone));

    // then
    resultActions.andExpect(status().isOk())
        .andDo(print());
  }
}