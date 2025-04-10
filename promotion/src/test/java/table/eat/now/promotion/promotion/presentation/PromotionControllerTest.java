package table.eat.now.promotion.promotion.presentation;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static table.eat.now.common.constant.UserInfoConstant.USER_ID_HEADER;
import static table.eat.now.common.constant.UserInfoConstant.USER_ROLE_HEADER;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.ws.rs.core.MediaType;
import java.math.BigDecimal;
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
import table.eat.now.promotion.promotion.application.dto.response.GetPromotionInfo;
import table.eat.now.promotion.promotion.application.service.PromotionService;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 10.
 */
@AutoConfigureMockMvc
@WebMvcTest(PromotionController.class)
@ActiveProfiles("test")
class PromotionControllerTest {


  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockitoBean
  private PromotionService promotionService;

  @DisplayName("promotionUuid로 프로모션을 단 건 조회한다.")
  @Test
  void promotion_uuid_find_controller_test() throws Exception {
    // given
    String promotionUuid = UUID.randomUUID().toString();
    GetPromotionInfo info = new GetPromotionInfo(
        1L,
        promotionUuid,
        "봄맞이 할인",
        "따뜻한 봄을 맞이하여 전 메뉴 2000원 할인",
        LocalDateTime.of(2025, 4, 1, 0, 0),
        LocalDateTime.of(2025, 4, 30, 23, 59),
        BigDecimal.valueOf(2000),
        "ACTIVE",
        "SEASON"
    );

    given(promotionService.findPromotion(promotionUuid)).willReturn(info);

    // when
    ResultActions resultActions = mockMvc.perform(
        get("/api/v1/promotions/{promotionUuid}", promotionUuid)
            .header("Authorization", "Bearer {ACCESS_TOKEN}")
            .header(USER_ID_HEADER, "1")
            .header(USER_ROLE_HEADER, "MASTER")
            .contentType(MediaType.APPLICATION_JSON)
    );

    // then
    resultActions.andExpect(status().isOk())
        .andExpect(jsonPath("$.promotionId").value(1L))
        .andExpect(jsonPath("$.promotionUuid").value(promotionUuid))
        .andExpect(jsonPath("$.promotionName").value("봄맞이 할인"))
        .andExpect(jsonPath("$.description").value("따뜻한 봄을 맞이하여 전 메뉴 2000원 할인"))
        .andExpect(jsonPath("$.startTime").value("2025-04-01T00:00:00"))
        .andExpect(jsonPath("$.endTime").value("2025-04-30T23:59:00"))
        .andExpect(jsonPath("$.discountAmount").value(2000))
        .andExpect(jsonPath("$.promotionStatus").value("ACTIVE"))
        .andExpect(jsonPath("$.promotionType").value("SEASON"))
        .andDo(print());
  }
}