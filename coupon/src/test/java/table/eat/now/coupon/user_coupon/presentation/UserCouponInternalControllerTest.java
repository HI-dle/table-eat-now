package table.eat.now.coupon.user_coupon.presentation;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static table.eat.now.common.constant.UserInfoConstant.USER_ID_HEADER;
import static table.eat.now.common.constant.UserInfoConstant.USER_ROLE_HEADER;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.IntStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import table.eat.now.common.resolver.dto.CurrentUserInfoDto;
import table.eat.now.common.resolver.dto.UserRole;
import table.eat.now.coupon.helper.ControllerTestSupport;
import table.eat.now.coupon.user_coupon.application.client.dto.response.GetCouponInfoI;
import table.eat.now.coupon.user_coupon.application.dto.response.GetUserCouponInfoI;
import table.eat.now.coupon.user_coupon.presentation.dto.request.PreemptUserCouponRequest;

class UserCouponInternalControllerTest extends ControllerTestSupport {

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

    doNothing().when(userCouponService).preemptUserCoupons(userInfo, request.toCommand());

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

  @DisplayName("사용자 쿠폰 목록 조회 요청 - 200 응답")
  @Test
  void getUserCoupons() throws Exception {
    // given
    CurrentUserInfoDto userInfo = CurrentUserInfoDto.of(2L, UserRole.CUSTOMER);
    List<String> userCouponUuids = List.of(UUID.randomUUID().toString(), UUID.randomUUID().toString());

    List<GetCouponInfoI> couponInfoIs = IntStream.range(0, 2)
        .mapToObj(i -> GetCouponInfoI.builder()
            .couponId((long) i)
            .couponUuid(UUID.randomUUID().toString())
            .type("FIXED_DISCOUNT")
            .label("PROMOTION")
            .allowDuplicate(false)
            .count(1000)
            .issueStartAt(LocalDate.now().atStartOfDay())
            .issueEndAt(LocalDate.now().plusDays(8).atStartOfDay())
            .amount(1000)
            .minPurchaseAmount(10000)
            .build())
        .toList();

    List<GetUserCouponInfoI> userCouponInfos = IntStream.range(0, 2)
        .mapToObj(i -> GetUserCouponInfoI.builder()
            .id((long) i)
            .userId(2L)
            .couponUuid(UUID.randomUUID().toString())
            .coupon(couponInfoIs.get(i))
            .userCouponUuid(userCouponUuids.get(i))
            .couponUuid(couponInfoIs.get(i).couponUuid())
            .name("test coupon name")
            .expiresAt(LocalDate.now().plusDays(4).atStartOfDay())
            .createdAt(LocalDate.now().minusDays(4).atStartOfDay())
            .createdBy(2L)
            .build())
        .toList();

    given(userCouponService.getUserCouponsInternalBy(new HashSet<>(userCouponUuids)))
        .willReturn(userCouponInfos);

    // when
    ResultActions resultActions = mockMvc.perform(
        get("/internal/v1/user-coupons")
            .header("Authorization", "Bearer {ACCESS_TOKEN}")
            .header(USER_ID_HEADER, userInfo.userId())
            .header(USER_ROLE_HEADER, userInfo.role())
            .param("userCouponUuids", userCouponUuids.get(0))
            .param("userCouponUuids", userCouponUuids.get(1))
            );

    // then
    resultActions.andExpect(status().isOk())
        .andExpect(jsonPath("$.userCoupons.size()").value(userCouponUuids.size()))
        .andExpect(jsonPath("$.userCoupons[0].coupon.couponUuid").value(couponInfoIs.get(0).couponUuid()))
        .andExpect(jsonPath("$.userCoupons[0].userId").value(userInfo.userId()))
        .andDo(print());
  }
}