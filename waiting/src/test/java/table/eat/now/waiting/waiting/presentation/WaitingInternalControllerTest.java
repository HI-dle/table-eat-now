package table.eat.now.waiting.waiting.presentation;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.ResultActions;
import table.eat.now.waiting.helper.ControllerTestSupport;
import table.eat.now.waiting.waiting.application.service.WaitingService;
import table.eat.now.waiting.waiting.application.service.dto.response.GetDailyWaitingInfo;

@WebMvcTest(WaitingInternalController.class)
class WaitingInternalControllerTest extends ControllerTestSupport {

  @MockitoBean
  private WaitingService waitingService;

  @DisplayName("일간 대기 정보 내부 조회 검증 - 200 성공")
  @Test
  void getDailyWaitingInfo() throws Exception {
    // given
    var info = GetDailyWaitingInfo.builder()
        .dailyWaitingUuid(UUID.randomUUID().toString())
        .restaurantUuid(UUID.randomUUID().toString())
        .restaurantName("혜주네 식당")
        .waitingDate(LocalDate.now())
        .avgWaitingSec(600L)
        .status("AVAILABLE")
        .totalSequence(null)
        .build();

    given(waitingService.getDailyWaitingInfo(eq(info.dailyWaitingUuid()))).willReturn(info);

    // when
    ResultActions resultActions = mockMvc.perform(
        get("/internal/v1/waitings/{dailyWaitingUuid}", info.dailyWaitingUuid()));

    // then
    resultActions.andExpect(status().isOk())
        .andExpect(jsonPath("$.dailyWaitingUuid").value(info.dailyWaitingUuid()))
        .andExpect(jsonPath("$.restaurantUuid").value(info.restaurantUuid()))
        .andExpect(jsonPath("$.restaurantName").value(info.restaurantName()))
        .andExpect(jsonPath("$.status").value(info.status()))
        .andDo(print());
  }
}