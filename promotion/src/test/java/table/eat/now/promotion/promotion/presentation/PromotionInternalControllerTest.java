package table.eat.now.promotion.promotion.presentation;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import jakarta.ws.rs.core.MediaType;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.ResultActions;
import table.eat.now.promotion.global.support.ControllerTestSupport;
import table.eat.now.promotion.promotion.application.dto.response.GetPromotionsClientInfo;
import table.eat.now.promotion.promotion.presentation.dto.request.GetPromotionsFeignRequest;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 13.
 */
class PromotionInternalControllerTest extends ControllerTestSupport {

  @DisplayName("레스토랑 아이디로 프로모션에 참여중인 식당과 내용을 조회해 반환한다.")
  @Test
  void reservation_get_promotions_test() throws Exception {
    // given
    GetPromotionsFeignRequest request = new GetPromotionsFeignRequest(
        Set.of("promo-uuid-1", "promo-uuid-2"),
        "restaurant-uuid-123"
    );

    GetPromotionsClientInfo mockResponse = GetPromotionsClientInfo.builder()
        .reservationRequests(List.of(
            GetPromotionsClientInfo.ReservationInfo.builder()
                .promotionId(1L)
                .promotionUuid("promo-uuid-1")
                .promotionName("봄맞이 할인 프로모션")
                .startTime(LocalDateTime.now().plusDays(1))
                .endTime(LocalDateTime.now().plusDays(10))
                .description("전 메뉴 3000원 할인")
                .discountPrice(BigDecimal.valueOf(3000))
                .promotionStatus("READY")
                .promotionType("COUPON")
                .promotionRestaurantUuid("promotion-restaurant-uuid-1")
                .restaurantUuid("restaurant-uuid-123")
                .build()
        ))
        .build();

    given(promotionService.reservationGetPromotions(request.toApplication()))
        .willReturn(mockResponse);

    // when
    ResultActions resultActions = mockMvc.perform(post("/internal/v1/promotions")
        .content(objectMapper.writeValueAsString(request))
        .contentType(MediaType.APPLICATION_JSON));

    // then
    resultActions.andExpect(status().isOk())
        .andExpect(jsonPath("$.reservationRequests").isArray())
        .andExpect(jsonPath("$.reservationRequests[0].promotionUuid").value("promo-uuid-1"))
        .andExpect(jsonPath("$.reservationRequests[0].restaurantUuid").value("restaurant-uuid-123"))
        .andDo(print());
  }


}