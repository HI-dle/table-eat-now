package table.eat.now.coupon.user_coupon.presentation;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static table.eat.now.common.constant.UserInfoConstant.USER_ID_HEADER;
import static table.eat.now.common.constant.UserInfoConstant.USER_ROLE_HEADER;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.ResultActions;
import table.eat.now.common.resolver.dto.CurrentUserInfoDto;
import table.eat.now.common.resolver.dto.UserRole;
import table.eat.now.coupon.helper.ControllerTestSupport;
import table.eat.now.coupon.user_coupon.application.dto.response.GetUserCouponInfo;
import table.eat.now.coupon.user_coupon.application.dto.response.PageResponse;
import table.eat.now.coupon.user_coupon.application.service.UserCouponService;
import table.eat.now.coupon.user_coupon.fixture.UserCouponFixture;

@WebMvcTest(UserCouponApiController.class)
class UserCouponApiControllerTest extends ControllerTestSupport {
  @MockitoBean
  private UserCouponService userCouponService;

  @DisplayName("사용자별 쿠폰 조회 성공 - 200 응답")
  @Test
  void getUserCouponsByUserId() throws Exception {

    // given
    CurrentUserInfoDto userInfo = CurrentUserInfoDto.of(2L, UserRole.CUSTOMER);
    Pageable pageable = PageRequest.of(0, 10, Sort.by("expiresAt").ascending());
    List<GetUserCouponInfo> userCouponInfos = UserCouponFixture.createGetUserCouponInfoList(10, userInfo.userId());
    PageResponse<GetUserCouponInfo> userCouponInfoPage =
        PageResponse.of(userCouponInfos, 10, 1, 1, 10);

    given(userCouponService.getUserCouponsByUserId(any(CurrentUserInfoDto.class), any(Pageable.class)))
        .willReturn(userCouponInfoPage);

    // when
    ResultActions resultActions = mockMvc.perform(
        get("/api/v1/user-coupons")
            .header("Authorization", "Bearer {ACCESS_TOKEN}")
            .header(USER_ID_HEADER, userInfo.userId())
            .header(USER_ROLE_HEADER, userInfo.role()));

    // then
    resultActions.andExpect(status().isOk())
        .andExpect(jsonPath("$.coupons.length()").value(10))
        .andExpect(jsonPath("$.coupons[0].userId").value(2L))
        .andDo(print());
  }
}