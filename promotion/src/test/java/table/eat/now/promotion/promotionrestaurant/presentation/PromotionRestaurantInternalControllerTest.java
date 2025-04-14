package table.eat.now.promotion.promotionrestaurant.presentation;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static table.eat.now.common.constant.UserInfoConstant.USER_ID_HEADER;
import static table.eat.now.common.constant.UserInfoConstant.USER_ROLE_HEADER;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.ws.rs.core.MediaType;
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
import table.eat.now.promotion.promotionrestaurant.application.dto.response.GetPromotionRestaurantInfo;
import table.eat.now.promotion.promotionrestaurant.application.service.PromotionRestaurantService;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 13.
 */
@AutoConfigureMockMvc
@WebMvcTest(PromotionRestaurantInternalController.class)
@ActiveProfiles("test")
class PromotionRestaurantInternalControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockitoBean
  private PromotionRestaurantService promotionRestaurantService;

  @DisplayName("레스토랑 UUID로 프로모션-레스토랑 정보 조회 테스트")
  @Test
  void promotion_restaurant_find_by_restaurant_uuid_test() throws Exception {
    // given
    UUID restaurantUuid = UUID.randomUUID();
    UUID promotionUuid = UUID.randomUUID();

    GetPromotionRestaurantInfo responseDto = new GetPromotionRestaurantInfo(
        "promotion-restaurant-uuid-1",
        promotionUuid.toString(),
        restaurantUuid.toString()
    );

    given(promotionRestaurantService.findRestaurantsByPromotions(restaurantUuid.toString(),
        promotionUuid.toString()))
        .willReturn(responseDto);

    // when
    ResultActions resultActions = mockMvc.perform(
        get("/internal/v1/promotion-restaurants/{restaurantUuid}/promotion/{promotionUuid}",
            restaurantUuid, promotionUuid)
        .header("Authorization", "Bearer {ACCESS_TOKEN}")
        .header(USER_ID_HEADER, "1")
        .header(USER_ROLE_HEADER, "MASTER")
        .contentType(MediaType.APPLICATION_JSON));

    // then
    resultActions.andExpect(status().isOk())
        .andExpect(jsonPath("$.promotionRestaurantUuid").value("promotion-restaurant-uuid-1"))
        .andExpect(jsonPath("$.promotionUuid").value(promotionUuid.toString()))
        .andExpect(jsonPath("$.restaurantUuid").value(restaurantUuid.toString()))
        .andDo(print());
  }


}