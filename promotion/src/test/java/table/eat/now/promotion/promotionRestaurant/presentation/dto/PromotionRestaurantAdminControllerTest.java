package table.eat.now.promotion.promotionRestaurant.presentation.dto;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
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
import table.eat.now.promotion.promotionRestaurant.application.dto.request.UpdatePromotionRestaurantCommand;
import table.eat.now.promotion.promotionRestaurant.application.dto.response.CreatePromotionRestaurantInfo;
import table.eat.now.promotion.promotionRestaurant.application.dto.response.UpdatePromotionRestaurantInfo;
import table.eat.now.promotion.promotionRestaurant.application.service.PromotionRestaurantService;
import table.eat.now.promotion.promotionRestaurant.domain.entity.PromotionRestaurant;
import table.eat.now.promotion.promotionRestaurant.presentation.PromotionRestaurantAdminController;
import table.eat.now.promotion.promotionRestaurant.presentation.dto.request.CreatePromotionRestaurantRequest;
import table.eat.now.promotion.promotionRestaurant.presentation.dto.request.UpdatePromotionRestaurantRequest;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 08.
 */
@AutoConfigureMockMvc
@WebMvcTest(PromotionRestaurantAdminController.class)
@ActiveProfiles("test")
class PromotionRestaurantAdminControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockitoBean
  private PromotionRestaurantService promotionRestaurantService;

  @DisplayName("프로모션 식당 생성 테스트")
  @Test
  void promotion_restaurant_create_test() throws Exception {
      // given
    CreatePromotionRestaurantRequest request = new CreatePromotionRestaurantRequest(
        UUID.randomUUID().toString(),
        UUID.randomUUID().toString()
    );

    PromotionRestaurant entity = request.toApplication().toEntity();

    given(promotionRestaurantService.createPromotionRestaurant(request.toApplication()))
        .willReturn(CreatePromotionRestaurantInfo.from(entity));

    // when
    ResultActions resultActions = mockMvc.perform(post("/admin/v1/promotion-restaurants")
        .header("Authorization", "Bearer {ACCESS_TOKEN}")
        .header(USER_ID_HEADER, "1")
        .header(USER_ROLE_HEADER, "MASTER")
        .content(objectMapper.writeValueAsString(request))
        .contentType(MediaType.APPLICATION_JSON));

      // then
    resultActions.andExpect(status().isCreated())
        .andExpect(header().string("Location", "/admin/v1/promotion-restaurants"))
        .andDo(print());
  }
  @DisplayName("promotionRestaurantUuid로 프로모션-레스토랑 정보를 수정한다.")
  @Test
  void update_promotion_restaurant_controller_test() throws Exception {
    // given
    String promotionRestaurantUuid = UUID.randomUUID().toString();

    UpdatePromotionRestaurantRequest request = new UpdatePromotionRestaurantRequest(
        "promotion-uuid",
        "restaurant-uuid"
    );

    UpdatePromotionRestaurantInfo info = new UpdatePromotionRestaurantInfo(
        promotionRestaurantUuid,
        request.promotionUuid(),
        request.restaurantUuid()
    );

    given(promotionRestaurantService.updatePromotionRestaurant(
        any(UpdatePromotionRestaurantCommand.class),
        eq(promotionRestaurantUuid)))
        .willReturn(info);

    // when
    ResultActions resultActions = mockMvc.perform(
        put("/admin/v1/promotion-restaurants/{promotionRestaurantUuid}",
            promotionRestaurantUuid)
        .header("Authorization", "Bearer {ACCESS_TOKEN}")
        .header(USER_ID_HEADER, "1")
        .header(USER_ROLE_HEADER, "MASTER")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)));

    // then
    resultActions
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.promotionRestaurantUuid").value(promotionRestaurantUuid))
        .andExpect(jsonPath("$.promotionUuid").value("promotion-uuid"))
        .andExpect(jsonPath("$.restaurantUuid").value("restaurant-uuid"))
        .andDo(print());
  }



}