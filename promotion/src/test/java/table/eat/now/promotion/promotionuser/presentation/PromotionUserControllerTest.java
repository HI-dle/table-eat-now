package table.eat.now.promotion.promotionuser.presentation;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static table.eat.now.common.constant.UserInfoConstant.USER_ID_HEADER;
import static table.eat.now.common.constant.UserInfoConstant.USER_ROLE_HEADER;

import jakarta.ws.rs.core.MediaType;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.ResultActions;
import table.eat.now.promotion.global.support.ControllerTestSupport;
import table.eat.now.promotion.promotionuser.application.dto.response.CreatePromotionUserInfo;
import table.eat.now.promotion.promotionuser.domain.entity.PromotionUser;
import table.eat.now.promotion.promotionuser.presentation.dto.request.CreatePromotionUserRequest;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 08.
 */
class PromotionUserControllerTest extends ControllerTestSupport {

  @DisplayName("프로모션 유저 생성 테스트")
  @Test
  void promotion_user_create_test() throws Exception {
    // given
    UUID promotionUuid = UUID.randomUUID();
    CreatePromotionUserRequest request = new CreatePromotionUserRequest(1L,promotionUuid);

    PromotionUser entity = request.toApplication().toEntity();

    given(promotionUserService.createPromotionUser(request.toApplication()))
        .willReturn(CreatePromotionUserInfo.from(entity));

    // when
    ResultActions resultActions = mockMvc.perform(post("/api/v1/promotion-users")
        .header("Authorization", "Bearer {ACCESS_TOKEN}")
        .header(USER_ID_HEADER, "1")
        .header(USER_ROLE_HEADER, "MASTER")
        .content(objectMapper.writeValueAsString(request))
        .contentType(MediaType.APPLICATION_JSON));

    // then
    resultActions.andExpect(status().isCreated())
        .andExpect(header().string("Location", "/api/v1/promotion-users"))
        .andDo(print());
  }

}