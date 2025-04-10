package table.eat.now.promotion.promotionUser.presentation;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
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
import table.eat.now.promotion.promotionUser.application.dto.PaginatedResultCommand;
import table.eat.now.promotion.promotionUser.application.dto.response.SearchPromotionUserInfo;
import table.eat.now.promotion.promotionUser.application.dto.response.UpdatePromotionUserInfo;
import table.eat.now.promotion.promotionUser.application.service.PromotionUserService;
import table.eat.now.promotion.promotionUser.presentation.dto.request.SearchPromotionUserRequest;
import table.eat.now.promotion.promotionUser.presentation.dto.request.UpdatePromotionUserRequest;

/**
 * @Date : 2025. 04. 10.
 * @author : hanjihoon
 */
@AutoConfigureMockMvc
@WebMvcTest(PromotionUserAdminController.class)
@ActiveProfiles("test")
class PromotionUserAdminControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockitoBean
  private PromotionUserService promotionUserService;

  @DisplayName("promotionUserUuid로 프로모션 유저 정보를 수정한다.")
  @Test
  void promotion_user_update_controller_test() throws Exception {
    // given
    String promotionUserUuid = UUID.randomUUID().toString();
    String promotionUuid = UUID.randomUUID().toString();
    UpdatePromotionUserRequest request = new UpdatePromotionUserRequest(1L,promotionUuid);

    UpdatePromotionUserInfo info = new UpdatePromotionUserInfo(
        promotionUserUuid, 1L, promotionUuid);

    given(promotionUserService.updatePromotionUser(request.toApplication(), promotionUserUuid))
        .willReturn(info);

    // when
    ResultActions resultActions = mockMvc.perform(
        put("/admin/v1/promotion-users/{promotionUserUuid}", promotionUserUuid)
            .header("Authorization", "Bearer {ACCESS_TOKEN}")
            .header(USER_ID_HEADER, "1")
            .header(USER_ROLE_HEADER, "MASTER")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)));

    // then
    resultActions.andExpect(status().isOk())
        .andExpect(jsonPath("$.promotionUserUuid").value(promotionUserUuid))
        .andExpect(jsonPath("$.userId").value(1))
        .andExpect(jsonPath("$.promotionUuid").value(promotionUuid))
        .andDo(print());
  }

  @DisplayName("유저 아이디와 프로모션 아이디로 프로모션에 참여한 유저 정보를 검색한다.")
  @Test
  void promotion_user_search_controller_test() throws Exception {
    // given
    String promotionUuid = UUID.randomUUID().toString();

    SearchPromotionUserRequest request = new SearchPromotionUserRequest(
        1L,
        promotionUuid,
        true,
        "createdAt",
        0,
        10
    );

    SearchPromotionUserInfo info1 = new SearchPromotionUserInfo(
        "promotion-user-uuid-1", promotionUuid, 1L);
    SearchPromotionUserInfo info2 = new SearchPromotionUserInfo(
        "promotion-user-uuid-2", promotionUuid, 1L);

    PaginatedResultCommand<SearchPromotionUserInfo> result = new PaginatedResultCommand<>(
        List.of(info1, info2),
        0,
        10,
        2L,
        1
    );

    given(promotionUserService.searchPromotionUser(request.toApplication()))
        .willReturn(result);

    // when
    ResultActions resultActions = mockMvc.perform(
        get("/admin/v1/promotion-users")
            .header("Authorization", "Bearer {ACCESS_TOKEN}")
            .header(USER_ID_HEADER, "1")
            .header(USER_ROLE_HEADER, "MASTER")
            .queryParam("userId", "1")
            .queryParam("promotionUuid", promotionUuid)
            .queryParam("isAsc", "true")
            .queryParam("sortBy", "createdAt")
            .queryParam("page", "0")
            .queryParam("size", "10")
            .contentType(MediaType.APPLICATION_JSON)
    );

    // then
    resultActions.andExpect(status().isOk())
        .andExpect(jsonPath("$.content[0].promotionUserUuid").value("promotion-user-uuid-1"))
        .andExpect(jsonPath("$.content[0].userId").value(1))
        .andExpect(jsonPath("$.content[0].promotionUuid").value(promotionUuid))
        .andExpect(jsonPath("$.content[1].promotionUserUuid").value("promotion-user-uuid-2"))
        .andExpect(jsonPath("$.content[1].userId").value(1))
        .andExpect(jsonPath("$.content[1].promotionUuid").value(promotionUuid))
        .andExpect(jsonPath("$.page").value(0))
        .andExpect(jsonPath("$.size").value(10))
        .andExpect(jsonPath("$.totalElements").value(2))
        .andExpect(jsonPath("$.totalPages").value(1))
        .andDo(print());
  }

  @DisplayName("userId로 프로모션 유저 정보를 삭제한다.")
  @Test
  void promotion_uuid_delete_promotion_restaurant_controller_test() throws Exception {
    // given
    Long userId = 1L;

    willDoNothing().given(promotionUserService)
        .deletePromotionUser(eq(userId), any(CurrentUserInfoDto.class));

    // when
    ResultActions resultActions = mockMvc.perform(delete(
        "/admin/v1/promotion-users/{userId}", userId)
        .header("Authorization", "Bearer {ACCESS_TOKEN}")
        .header(USER_ID_HEADER, "1")
        .header(USER_ROLE_HEADER, "MASTER"));

    // then
    resultActions.andExpect(status().isNoContent())
        .andDo(print());

    verify(promotionUserService).deletePromotionUser(eq(userId), any());
  }


}