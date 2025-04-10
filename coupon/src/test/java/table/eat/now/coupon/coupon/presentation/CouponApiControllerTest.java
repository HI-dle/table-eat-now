package table.eat.now.coupon.coupon.presentation;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static table.eat.now.common.constant.UserInfoConstant.USER_ID_HEADER;
import static table.eat.now.common.constant.UserInfoConstant.USER_ROLE_HEADER;

import java.util.UUID;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.ResultActions;
import table.eat.now.common.resolver.dto.CurrentUserInfoDto;
import table.eat.now.common.resolver.dto.UserRole;
import table.eat.now.coupon.coupon.application.service.CouponService;
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
}