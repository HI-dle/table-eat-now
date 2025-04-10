package table.eat.now.promotion.promotionUser.presentation;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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
import table.eat.now.promotion.promotionUser.application.dto.response.UpdatePromotionUserInfo;
import table.eat.now.promotion.promotionUser.application.service.PromotionUserService;
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
}