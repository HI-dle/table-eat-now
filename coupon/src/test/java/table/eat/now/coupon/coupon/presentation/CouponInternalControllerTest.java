package table.eat.now.coupon.coupon.presentation;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static table.eat.now.common.constant.UserInfoConstant.USER_ID_HEADER;
import static table.eat.now.common.constant.UserInfoConstant.USER_ROLE_HEADER;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
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
import table.eat.now.coupon.coupon.application.dto.response.GetCouponsInfoI;
import table.eat.now.coupon.coupon.application.service.CouponService;
import table.eat.now.coupon.coupon.domain.entity.Coupon;
import table.eat.now.coupon.coupon.fixture.CouponFixture;

@AutoConfigureMockMvc
@WebMvcTest(CouponInternalController.class)
@ActiveProfiles("test")
class CouponInternalControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockitoBean
  private CouponService couponService;

  @BeforeEach
  void setUp() {
  }

  @DisplayName("쿠폰 내부 단건 조회 요청 검증 - 200 응답")
  @Test
  void getCouponInternal() throws Exception {
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
        get("/internal/v1/coupons/{couponUuid}", couponUuid.toString())
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

  @DisplayName("쿠폰 내부 다건 조회 요청 검증 - 200 응답")
  @Test
  void getCouponsInternal() throws Exception {
    // given
    List<Coupon> coupons = CouponFixture.createCoupons(3);
    GetCouponsInfoI getCouponsInfo = GetCouponsInfoI.from(coupons);
    Set<String> couponUuidsStr = coupons.stream()
        .map(Coupon::getCouponUuid)
        .collect(Collectors.toSet());
    Set<UUID> couponUuids = couponUuidsStr.stream()
        .map(UUID::fromString)
        .collect(Collectors.toSet());

    given(couponService.getCouponsInternal(couponUuids)).willReturn(getCouponsInfo);

    // when
    ResultActions resultActions = mockMvc.perform(
        get("/internal/v1/coupons")
            .param("couponUuids", couponUuidsStr.toArray(new String[0]))
            .header("Authorization", "Bearer {ACCESS_TOKEN}")
            .header(USER_ID_HEADER, "1")
            .header(USER_ROLE_HEADER, "MASTER"));

    // then
    resultActions.andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.coupons.length()").value(3))
        .andExpect(jsonPath("$.coupons[0].type").value("FIXED_DISCOUNT"))
        .andDo(print());
  }
}