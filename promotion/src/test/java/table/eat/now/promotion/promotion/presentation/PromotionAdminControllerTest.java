package table.eat.now.promotion.promotion.presentation;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
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
import table.eat.now.common.resolver.dto.CurrentUserInfoDto;
import table.eat.now.promotion.promotion.application.dto.request.UpdatePromotionCommand;
import table.eat.now.promotion.promotion.application.dto.response.CreatePromotionInfo;
import table.eat.now.promotion.promotion.application.dto.response.UpdatePromotionInfo;
import table.eat.now.promotion.promotion.application.service.PromotionService;
import table.eat.now.promotion.promotion.domain.entity.Promotion;
import table.eat.now.promotion.promotion.domain.entity.PromotionStatus;
import table.eat.now.promotion.promotion.domain.entity.PromotionType;
import table.eat.now.promotion.promotion.presentation.dto.request.CreatePromotionRequest;
import table.eat.now.promotion.promotion.presentation.dto.request.UpdatePromotionRequest;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 08.
 */
@AutoConfigureMockMvc
@WebMvcTest(PromotionAdminController.class)
@ActiveProfiles("test")
class PromotionAdminControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockitoBean
  private PromotionService promotionService;


  @DisplayName("프로모션 생성 테스트")
  @Test
  void promotion_create_test () throws Exception {
    String couponUuid = UUID.randomUUID().toString();
    // given
    CreatePromotionRequest request = new CreatePromotionRequest(
        couponUuid,
        "봄맞이 할인 프로모션",
        "전 메뉴 3000원 할인",
        LocalDateTime.now().plusDays(1),
        LocalDateTime.now().plusDays(10),
        BigDecimal.valueOf(3000),
        "READY",
        "COUPON"
    );
    Promotion entity = request.toApplication().toEntity();

    given(promotionService.createPromotion(request.toApplication()))
        .willReturn(CreatePromotionInfo.from(entity));
    // when
    ResultActions resultActions = mockMvc.perform(post("/admin/v1/promotions")
        .header("Authorization", "Bearer {ACCESS_TOKEN}")
        .header(USER_ID_HEADER, "1")
        .header(USER_ROLE_HEADER, "MASTER")
        .content(objectMapper.writeValueAsString(request))
        .contentType(MediaType.APPLICATION_JSON));

    // then
    resultActions.andExpect(status().isCreated())
        .andExpect(header().string("Location", "/admin/v1/promotions"))
        .andDo(print());
  }
  @DisplayName("promotionUuid로 프로모션 내용을 수정한다.")
  @Test
  void promotion_uuid_update_controller_test() throws Exception {
    // given
    String promotionUuid = UUID.randomUUID().toString();
    String couponUuid = UUID.randomUUID().toString();

    UpdatePromotionRequest request = new UpdatePromotionRequest(
        couponUuid,
        "봄맞이 할인 프로모션 - 수정 후",
        "전 메뉴 5000원 할인",
        LocalDateTime.now().plusDays(2),
        LocalDateTime.now().plusDays(12),
        BigDecimal.valueOf(5000),
        "READY",
        "COUPON"
    );

    UpdatePromotionCommand command = request.toApplication();

    Promotion promotion = Promotion.of(
        couponUuid,
        "봄맞이 할인 프로모션 - 수정 전",
        "전 메뉴 5000원 할인",
        LocalDateTime.now().plusDays(2),
        LocalDateTime.now().plusDays(12),
        BigDecimal.valueOf(5000),
        PromotionStatus.valueOf("READY"),
        PromotionType.valueOf("COUPON")
    );

    promotion.modifyPromotion(
        command.couponUuid(),
        command.promotionName(),
        command.description(),
        command.startTime(),
        command.endTime(),
        command.discountAmount(),
        PromotionStatus.valueOf(command.promotionStatus()),
        PromotionType.valueOf(command.promotionType()));

    given(promotionService.updatePromotion(request.toApplication(), promotionUuid))
        .willReturn(UpdatePromotionInfo.from(promotion));

    // when
    ResultActions resultActions = mockMvc.perform(put(
        "/admin/v1/promotions/{promotionUuid}", promotionUuid)
        .header("Authorization", "Bearer {ACCESS_TOKEN}")
        .header(USER_ID_HEADER, "1")
        .header(USER_ROLE_HEADER, "MASTER")
        .content(objectMapper.writeValueAsString(request))
        .contentType(MediaType.APPLICATION_JSON));

    // then
    resultActions.andExpect(status().isOk())
        .andExpect(jsonPath("$.couponUuid").value(couponUuid))
        .andExpect(jsonPath("$.promotionName").value("봄맞이 할인 프로모션 - 수정 후"))
        .andExpect(jsonPath("$.description").value("전 메뉴 5000원 할인"))
        .andExpect(jsonPath("$.discountAmount").value(5000))
        .andExpect(jsonPath("$.promotionStatus").value("READY"))
        .andExpect(jsonPath("$.promotionType").value("COUPON"))
        .andDo(print());
  }

  @DisplayName("promotionUuid로 프로모션을 삭제한다.")
  @Test
  void promotion_uuid_delete_controller_test() throws Exception {
    // given
    String promotionUuid = UUID.randomUUID().toString();

    willDoNothing().given(promotionService)
        .deletePromotion(eq(promotionUuid), any(CurrentUserInfoDto.class));

    // when
    ResultActions resultActions = mockMvc.perform(delete(
        "/admin/v1/promotions/{promotionUuid}", promotionUuid)
        .header("Authorization", "Bearer {ACCESS_TOKEN}")
        .header(USER_ID_HEADER, "1")
        .header(USER_ROLE_HEADER, "MASTER"));

    // then
    resultActions.andExpect(status().isNoContent())
        .andDo(print());

    verify(promotionService).deletePromotion(eq(promotionUuid), any());
  }

}


