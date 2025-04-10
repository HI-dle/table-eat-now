package table.eat.now.promotion.promotionRestaurant.presentation.dto;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
import java.util.List;
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
import table.eat.now.common.resolver.dto.CurrentUserInfoDto;
import table.eat.now.promotion.promotionRestaurant.application.dto.PaginatedResultCommand;
import table.eat.now.promotion.promotionRestaurant.application.dto.request.SearchPromotionRestaurantCommand;
import table.eat.now.promotion.promotionRestaurant.application.dto.request.UpdatePromotionRestaurantCommand;
import table.eat.now.promotion.promotionRestaurant.application.dto.response.CreatePromotionRestaurantInfo;
import table.eat.now.promotion.promotionRestaurant.application.dto.response.SearchPromotionRestaurantInfo;
import table.eat.now.promotion.promotionRestaurant.application.dto.response.UpdatePromotionRestaurantInfo;
import table.eat.now.promotion.promotionRestaurant.application.service.PromotionRestaurantService;
import table.eat.now.promotion.promotionRestaurant.domain.entity.PromotionRestaurant;
import table.eat.now.promotion.promotionRestaurant.presentation.PromotionRestaurantAdminController;
import table.eat.now.promotion.promotionRestaurant.presentation.dto.request.CreatePromotionRestaurantRequest;
import table.eat.now.promotion.promotionRestaurant.presentation.dto.request.SearchPromotionRestaurantRequest;
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

  @DisplayName("프로모션-레스토랑 목록을 조회한다.")
  @Test
  void search_promotion_restaurant_controller_test() throws Exception {
    // given
    SearchPromotionRestaurantRequest request = new SearchPromotionRestaurantRequest(
        "promotion-uuid",
        "restaurant-uuid",
        true,
        "createdAt",
        0,
        10
    );

    List<SearchPromotionRestaurantInfo> content = List.of(
        new SearchPromotionRestaurantInfo(
            "pr-uuid-1", "promotion-uuid", "restaurant-uuid"),
        new SearchPromotionRestaurantInfo(
            "pr-uuid-2", "promotion-uuid", "restaurant-uuid")
    );

    PaginatedResultCommand<SearchPromotionRestaurantInfo> command =
        new PaginatedResultCommand<>(content, 0, 10, 2L, 1);

    given(promotionRestaurantService.searchPromotionRestaurant(
        any(SearchPromotionRestaurantCommand.class)))
        .willReturn(command);

    // when
    ResultActions resultActions = mockMvc.perform(
        get("/admin/v1/promotion-restaurants")
            .param("promotionUuid", "promotion-uuid")
            .param("restaurantUuid", "restaurant-uuid")
            .param("isAsc", "true")
            .param("sortBy", "createdAt")
            .param("page", "0")
            .param("size", "10")
            .header("Authorization", "Bearer {ACCESS_TOKEN}")
            .header(USER_ID_HEADER, "1")
            .header(USER_ROLE_HEADER, "MASTER")
    );

    // then
    resultActions
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content").isArray())
        .andExpect(jsonPath("$.content[0].promotionRestaurantUuid").value("pr-uuid-1"))
        .andExpect(jsonPath("$.content[0].promotionUuid").value("promotion-uuid"))
        .andExpect(jsonPath("$.content[0].restaurantUuid").value("restaurant-uuid"))
        .andExpect(jsonPath("$.page").value(0))
        .andExpect(jsonPath("$.size").value(10))
        .andExpect(jsonPath("$.totalElements").value(2))
        .andExpect(jsonPath("$.totalPages").value(1))
        .andDo(print());
  }

  @DisplayName("promotionUuid로 프로모션-레스토랑을 삭제한다.")
  @Test
  void promotion_uuid_delete_promotion_restaurant_controller_test() throws Exception {
    // given
    String promotionUuid = UUID.randomUUID().toString();

    willDoNothing().given(promotionRestaurantService)
        .deletePromotionRestaurant(eq(promotionUuid), any(CurrentUserInfoDto.class));

    // when
    ResultActions resultActions = mockMvc.perform(delete(
        "/admin/v1/promotion-restaurants/{promotionUuid}", promotionUuid)
        .header("Authorization", "Bearer {ACCESS_TOKEN}")
        .header(USER_ID_HEADER, "1")
        .header(USER_ROLE_HEADER, "MASTER"));

    // then
    resultActions.andExpect(status().isNoContent())
        .andDo(print());

    verify(promotionRestaurantService).deletePromotionRestaurant(eq(promotionUuid), any());
  }



}