package table.eat.now.coupon.coupon.presentation;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static table.eat.now.common.constant.UserInfoConstant.USER_ID_HEADER;
import static table.eat.now.common.constant.UserInfoConstant.USER_ROLE_HEADER;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import table.eat.now.coupon.coupon.application.service.CouponService;
import table.eat.now.coupon.coupon.presentation.dto.request.CreateCouponRequest;
import table.eat.now.coupon.coupon.presentation.dto.request.CreateCouponRequest.CouponType;

@AutoConfigureMockMvc
@WebMvcTest(CouponController.class)
@ActiveProfiles("test")
class CouponControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockitoBean
  private CouponService couponService;

  @BeforeEach
  void setUp() {
  }

  @DisplayName("쿠폰 생성 요청 검증 - 201 응답")
  @Test
  void createCoupon() throws Exception {
    // given
    CreateCouponRequest request = CreateCouponRequest.builder()
        .name("test")
        .type(CouponType.FIXED_DISCOUNT)
        .startAt(LocalDateTime.now().minusDays(2))
        .endAt(LocalDateTime.now().plusDays(2))
        .count(10000)
        .allowDuplicate(false)
        .minPurchaseAmount(50000)
        .amount(3000)
        .percent(null)
        .maxDiscountAmount(null)
        .build();

    UUID couponUuid = UUID.randomUUID();
    given(couponService.createCoupon(request.toCommand())).willReturn(couponUuid);

    // when
    ResultActions resultActions = mockMvc.perform(post("/api/v1/coupons")
        .header("Authorization", "Bearer {ACCESS_TOKEN}")
        .header(USER_ID_HEADER, "1")
        .header(USER_ROLE_HEADER, "MASTER")
        .content(objectMapper.writeValueAsString(request))
        .contentType(MediaType.APPLICATION_JSON));

    // then
    resultActions.andExpect(status().isCreated())
        .andExpect(header().string(
            "Location", String.format("/api/v1/coupons/%s", couponUuid)))
        .andDo(print());
  }
}