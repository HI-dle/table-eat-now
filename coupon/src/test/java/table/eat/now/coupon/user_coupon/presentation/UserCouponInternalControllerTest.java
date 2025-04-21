package table.eat.now.coupon.user_coupon.presentation;

import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static table.eat.now.common.constant.UserInfoConstant.USER_ID_HEADER;
import static table.eat.now.common.constant.UserInfoConstant.USER_ROLE_HEADER;

import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.ResultActions;
import table.eat.now.common.resolver.dto.CurrentUserInfoDto;
import table.eat.now.common.resolver.dto.UserRole;
import table.eat.now.coupon.helper.ControllerTestSupport;
import table.eat.now.coupon.user_coupon.application.service.UserCouponService;
import table.eat.now.coupon.user_coupon.presentation.dto.request.PreemptUserCouponRequest;

@WebMvcTest(UserCouponInternalController.class)
class UserCouponInternalControllerTest extends ControllerTestSupport {

  @MockitoBean
  private UserCouponService userCouponService;

  @BeforeEach
  void setUp() {
  }

  @DisplayName("사용자 쿠폰 선점 요청 - 200 응답")
  @Test
  void preemptUserCoupon() throws Exception {
    // given
    CurrentUserInfoDto userInfo = CurrentUserInfoDto.of(2L, UserRole.CUSTOMER);
    PreemptUserCouponRequest request = PreemptUserCouponRequest.builder()
        .reservationUuid(UUID.randomUUID())
        .userCouponUuids(Set.of(UUID.randomUUID(), UUID.randomUUID()))
        .build();

    doNothing().when(userCouponService).preemptUserCoupon(userInfo, request.toCommand());

    // when
    ResultActions resultActions = mockMvc.perform(
        patch("/internal/v1/user-coupons/preempt")
            .header("Authorization", "Bearer {ACCESS_TOKEN}")
            .header(USER_ID_HEADER, userInfo.userId())
            .header(USER_ROLE_HEADER, userInfo.role())
            .content(objectMapper.writeValueAsString(request))
            .contentType(MediaType.APPLICATION_JSON));

    // then
    resultActions.andExpect(status().isOk())
        .andDo(print());
  }
}