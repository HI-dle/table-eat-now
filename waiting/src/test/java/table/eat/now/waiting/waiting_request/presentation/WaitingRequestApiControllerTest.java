package table.eat.now.waiting.waiting_request.presentation;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
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
import table.eat.now.waiting.helper.ControllerTestSupport;
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
    var request = CreateWaitingRequestRequest.builder()
        .dailyWaitingUuid(UUID.randomUUID())
        .phone("01000000000")
        .slackId("slackId@example.com")
        .seatSize(3)
        .build();
    var waitingRequestUuid = UUID.randomUUID().toString();

    given(waitingRequestService.createWaitingRequest(
        any(CurrentUserInfoDto.class), eq(request.toCommand())))
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
}