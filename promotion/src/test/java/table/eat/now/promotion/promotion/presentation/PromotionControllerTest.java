package table.eat.now.promotion.promotion.presentation;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static table.eat.now.common.constant.UserInfoConstant.USER_ID_HEADER;
import static table.eat.now.common.constant.UserInfoConstant.USER_ROLE_HEADER;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.ws.rs.core.MediaType;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
import table.eat.now.promotion.promotion.application.dto.PaginatedResultCommand;
import table.eat.now.promotion.promotion.application.dto.request.ParticipatePromotionUserInfo;
import table.eat.now.promotion.promotion.application.dto.request.SearchPromotionCommand;
import table.eat.now.promotion.promotion.application.dto.response.GetPromotionInfo;
import table.eat.now.promotion.promotion.application.dto.response.SearchPromotionInfo;
import table.eat.now.promotion.promotion.application.service.PromotionService;
import table.eat.now.promotion.promotion.presentation.dto.request.ParticipatePromotionUserRequest;
import table.eat.now.promotion.promotion.presentation.dto.request.SearchPromotionRequest;

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
  @DisplayName("검색 결과에 따라 프로모션에 대한 결과 값을 반환합니다.")
  @Test
  void search_promotions_controller_test() throws Exception {
    // given
    SearchPromotionRequest request = new SearchPromotionRequest(
        "할인",
        "시즌",
        LocalDateTime.now().plusDays(1),
        LocalDateTime.now().plusDays(5),
        BigDecimal.valueOf(1000),
        "READY",
        "COUPON",
        true,
        "startTime",
        0,
        2
    );

    SearchPromotionCommand command = request.toApplication();

    SearchPromotionInfo info1 = new SearchPromotionInfo(
        1L,
        UUID.randomUUID().toString(),
        "봄맞이 할인",
        "봄 시즌 한정 할인",
        LocalDateTime.now().plusDays(1),
        LocalDateTime.now().plusDays(3),
        BigDecimal.valueOf(1000),
        "READY",
        "COUPON"
    );

    SearchPromotionInfo info2 = new SearchPromotionInfo(
        2L,
        UUID.randomUUID().toString(),
        "여름맞이 할인",
        "여름 시즌 한정 할인",
        LocalDateTime.now().plusDays(1),
        LocalDateTime.now().plusDays(4),
        BigDecimal.valueOf(2000),
        "READY",
        "COUPON"
    );

    var serviceResult = new PaginatedResultCommand<>(
        List.of(info1, info2),
        request.page(),
        request.size(),
        2L,
        1
    );

    given(promotionService.searchPromotion(eq(command)))
        .willReturn(serviceResult);

    //when then
    DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    mockMvc.perform(get("/api/v1/promotions")
            .param("promotionName", request.promotionName())
            .param("description", request.description())
            .param("startTime", request.startTime().format(formatter))
            .param("endTime", request.endTime().format(formatter))
            .param("discountAmount", request.discountAmount().toString())
            .param("promotionStatus", request.promotionStatus())
            .param("promotionType", request.promotionType())
            .param("isAsc", request.isAsc().toString())
            .param("sortBy", request.sortBy())
            .param("page", String.valueOf(request.page()))
            .param("size", String.valueOf(request.size()))
            .header("Authorization", "Bearer {ACCESS_TOKEN}")
            .header(USER_ID_HEADER, "1")
            .header(USER_ROLE_HEADER, "MASTER")
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content", org.hamcrest.Matchers.hasSize(2)))
        .andExpect(jsonPath("$.page").value(request.page()))
        .andExpect(jsonPath("$.size").value(request.size()))
        .andExpect(jsonPath("$.totalElements").value(2))
        .andExpect(jsonPath("$.totalPages").value(1))
        .andExpect(jsonPath("$.content[0].promotionName").value("봄맞이 할인"))
        .andExpect(jsonPath("$.content[1].promotionName").value("여름맞이 할인"))
        .andDo(print());

  }

  @DisplayName("유저가 프로모션에 정상적으로 참여하면 200 OK를 반환합니다.")
  @Test
  void participate_promotion_success_test() throws Exception {
    // given
    ParticipatePromotionUserRequest request = new ParticipatePromotionUserRequest(
        1L,
        UUID.randomUUID().toString(),
        "봄맞이 프로모션"
    );

    ParticipatePromotionUserInfo command = request.toApplication();

    given(promotionService.participate(eq(command)))
        .willReturn(true);

    // when then
    mockMvc.perform(post("/api/v1/promotions/event/participate")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request))
        )
        .andExpect(status().isOk())
        .andExpect(content().string(request.promotionName() + "에 참여 성공했습니다."))
        .andDo(print());
  }

  @DisplayName("프로모션 정원이 마감되면 429 TOO_MANY_REQUESTS 를 반환합니다.")
  @Test
  void participate_promotion_fail_test() throws Exception {
    // given
    ParticipatePromotionUserRequest request = new ParticipatePromotionUserRequest(
        2L,
        UUID.randomUUID().toString(),
        "여름맞이 프로모션"
    );

    ParticipatePromotionUserInfo command = request.toApplication();

    given(promotionService.participate(eq(command)))
        .willReturn(false);

    // when then
    mockMvc.perform(post("/api/v1/promotions/event/participate")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request))
        )
        .andExpect(status().isTooManyRequests())
        .andExpect(content().string("정원이 마감되었습니다."))
        .andDo(print());
  }


}