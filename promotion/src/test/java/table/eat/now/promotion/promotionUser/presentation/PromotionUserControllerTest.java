package table.eat.now.promotion.promotionUser.presentation;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
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
import table.eat.now.promotion.promotionUser.application.dto.response.CreatePromotionUserInfo;
import table.eat.now.promotion.promotionUser.application.service.PromotionUserService;
import table.eat.now.promotion.promotionUser.domain.entity.PromotionUser;
import table.eat.now.promotion.promotionUser.presentation.dto.request.CreatePromotionUserRequest;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 08.
 */
@AutoConfigureMockMvc
@WebMvcTest(PromotionUserController.class)
@ActiveProfiles("test")
class PromotionUserControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockitoBean
  private PromotionUserService promotionUserService;

  @DisplayName("프로모션 유저 생성 테스트")
  @Test
  void promotion_user_create_test() throws Exception {
    // given
    String promotionUuid = UUID.randomUUID().toString();
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