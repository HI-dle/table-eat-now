package table.eat.now.coupon.coupon.presentation;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static table.eat.now.common.constant.UserInfoConstant.USER_ID_HEADER;
import static table.eat.now.common.constant.UserInfoConstant.USER_ROLE_HEADER;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import table.eat.now.common.resolver.dto.CurrentUserInfoDto;
import table.eat.now.common.resolver.dto.UserRole;
import table.eat.now.coupon.coupon.application.service.CouponService;
import table.eat.now.coupon.coupon.application.service.dto.response.IssuableCouponInfo;
import table.eat.now.coupon.coupon.application.service.dto.response.PageResponse;
import table.eat.now.coupon.coupon.fixture.CouponFixture;
import table.eat.now.coupon.helper.ControllerTestSupport;

@WebMvcTest(CouponApiController.class)
class CouponApiControllerTest extends ControllerTestSupport {

  @MockitoBean
  private CouponService couponService;

  @BeforeEach
  void setUp() {
  }

  @DisplayName("고객 쿠폰 발급 요청 - 201 응답")
  @Test
  void requestCouponIssue() throws Exception {

    // given
    String couponUuid = UUID.randomUUID().toString();
    CurrentUserInfoDto userInfo = CurrentUserInfoDto.of(2L, UserRole.CUSTOMER);
    String userCouponUuid = UUID.randomUUID().toString();

    when(couponService.requestCouponIssue(any(CurrentUserInfoDto.class), any(String.class)))
        .thenReturn(userCouponUuid);
    // when
    ResultActions resultActions = mockMvc.perform(
        post("/api/v1/coupons/{couponUuid}/issue", couponUuid)
            .header("Authorization", "Bearer {ACCESS_TOKEN}")
            .header(USER_ID_HEADER, userInfo.userId())
            .header(USER_ROLE_HEADER, userInfo.role()));

    // then
    resultActions.andExpect(status().isCreated())
        .andExpect(header().string(
            "Location", Matchers.endsWith(String.format("/api/v1/user-coupons/%s", userCouponUuid))))
        .andDo(print());
  }

  @DisplayName("가용 쿠폰 조회 요청 - 200 응답")
  @Test
  void getAvailableCoupons() throws Exception {

    // given
    List<IssuableCouponInfo> couponInfos = CouponFixture.createAvailableCouponInfos(20);
    PageResponse<IssuableCouponInfo> couponInfoPage = PageResponse.of(
        couponInfos, 20, 2, 1, 10);

    given(couponService.getAvailableGeneralCoupons(any(), any())).willReturn(couponInfoPage);

    // when
    MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    params.add("time", LocalDateTime.now().truncatedTo(ChronoUnit.DAYS).toString());

    ResultActions resultActions = mockMvc.perform(
        get("/api/v1/coupons/daily/general")
            .params(params));

    // then
    resultActions.andExpect(status().isOk())
      .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
      .andExpect(jsonPath("$.coupons").isArray())
      .andExpect(jsonPath("$.coupons.length()").value(couponInfos.size()))
      .andExpect(jsonPath("$.totalElements").value(20))
      .andExpect(jsonPath("$.coupons[0].couponUuid").value(couponInfos.get(0).couponUuid()))
      .andDo(print());
  }
}
