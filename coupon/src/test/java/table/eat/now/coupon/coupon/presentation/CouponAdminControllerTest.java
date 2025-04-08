package table.eat.now.coupon.coupon.presentation;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static table.eat.now.common.constant.UserInfoConstant.USER_ID_HEADER;
import static table.eat.now.common.constant.UserInfoConstant.USER_ROLE_HEADER;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.UUID;
import org.hamcrest.Matchers;
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
import table.eat.now.coupon.coupon.application.dto.response.GetCouponInfo;
import table.eat.now.coupon.coupon.application.service.CouponService;
import table.eat.now.coupon.coupon.presentation.dto.request.CreateCouponRequest;
import table.eat.now.coupon.coupon.presentation.dto.request.UpdateCouponRequest;

@AutoConfigureMockMvc
@WebMvcTest(CouponAdminController.class)
@ActiveProfiles("test")
class CouponAdminControllerTest {

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
        .type(CreateCouponRequest.CouponType.FIXED_DISCOUNT)
        .startAt(LocalDateTime.now().plusDays(1))
        .endAt(LocalDateTime.now().plusDays(5))
        .count(10000)
        .allowDuplicate(false)
        .minPurchaseAmount(50000)
        .amount(3000)
        .percent(null)
        .maxDiscountAmount(null)
        .build();

    String couponUuid = UUID.randomUUID().toString();
    given(couponService.createCoupon(request.toCommand())).willReturn(couponUuid);

    // when
    ResultActions resultActions = mockMvc.perform(post("/admin/v1/coupons")
        .header("Authorization", "Bearer {ACCESS_TOKEN}")
        .header(USER_ID_HEADER, "1")
        .header(USER_ROLE_HEADER, "MASTER")
        .content(objectMapper.writeValueAsString(request))
        .contentType(MediaType.APPLICATION_JSON));

    // then
    resultActions.andExpect(status().isCreated())
        .andExpect(header().string(
            "Location", Matchers.endsWith(String.format("/admin/v1/coupons/%s", couponUuid))))
        .andDo(print());
  }

  @DisplayName("쿠폰 수정 요청 검증 - 200 응답")
  @Test
  void updateCoupon() throws Exception {
    // given
    UpdateCouponRequest request = UpdateCouponRequest.builder()
        .name("test")
        .type(UpdateCouponRequest.CouponType.FIXED_DISCOUNT)
        .startAt(LocalDateTime.now().plusDays(1))
        .endAt(LocalDateTime.now().plusDays(5))
        .count(10000)
        .allowDuplicate(false)
        .minPurchaseAmount(50000)
        .amount(3000)
        .percent(null)
        .maxDiscountAmount(null)
        .build();

    UUID couponUuid = UUID.randomUUID();
    doNothing().when(couponService).updateCoupon(couponUuid, request.toCommand());

    // when
    ResultActions resultActions = mockMvc.perform(
        patch("/admin/v1/coupons/{couponUuid}", couponUuid.toString())
            .header("Authorization", "Bearer {ACCESS_TOKEN}")
            .header(USER_ID_HEADER, "1")
            .header(USER_ROLE_HEADER, "MASTER")
            .content(objectMapper.writeValueAsString(request))
            .contentType(MediaType.APPLICATION_JSON));

    // then
    resultActions.andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.couponUuid").value(couponUuid.toString()))
        .andDo(print());
  }

  @DisplayName("쿠폰 조회 요청 검증 - 200 응답")
  @Test
  void getCoupon() throws Exception {
    // given
    UUID couponUuid = UUID.randomUUID();
    GetCouponInfo couponInfo = GetCouponInfo.builder()
        .couponId(1L)
        .couponUuid(couponUuid.toString())
        .name("test")
        .type("FIXED_DISCOUNT")
        .startAt(LocalDateTime.now().plusDays(1))
        .endAt(LocalDateTime.now().plusDays(5))
        .count(10000)
        .allowDuplicate(false)
        .minPurchaseAmount(50000)
        .amount(3000)
        .percent(null)
        .maxDiscountAmount(null)
        .createdAt(LocalDateTime.now().minusHours(1))
        .createdBy(1L)
        .build();

    given(couponService.getCoupon(couponUuid)).willReturn(couponInfo);

    // when
    ResultActions resultActions = mockMvc.perform(
        get("/admin/v1/coupons/{couponUuid}", couponUuid.toString())
            .header("Authorization", "Bearer {ACCESS_TOKEN}")
            .header(USER_ID_HEADER, "1")
            .header(USER_ROLE_HEADER, "MASTER"));

    // then
    resultActions.andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.couponUuid").value(couponUuid.toString()))
        .andExpect(jsonPath("$.type").value("FIXED_DISCOUNT"))
        .andDo(print());
  }
}