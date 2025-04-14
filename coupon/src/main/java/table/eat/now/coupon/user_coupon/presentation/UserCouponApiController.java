package table.eat.now.coupon.user_coupon.presentation;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import table.eat.now.common.aop.annotation.AuthCheck;
import table.eat.now.common.resolver.annotation.CurrentUserInfo;
import table.eat.now.common.resolver.dto.CurrentUserInfoDto;
import table.eat.now.common.resolver.dto.UserRole;
import table.eat.now.coupon.user_coupon.application.dto.response.PageResponse;
import table.eat.now.coupon.user_coupon.application.dto.response.GetUserCouponInfo;
import table.eat.now.coupon.user_coupon.application.service.UserCouponService;
import table.eat.now.coupon.user_coupon.presentation.dto.response.GetUserCouponsResponse;

@RequiredArgsConstructor
@RequestMapping("/api/v1/user-coupons")
@RestController
public class UserCouponApiController {
  private final UserCouponService userCouponService;

  @AuthCheck(roles = {UserRole.CUSTOMER})
  @GetMapping
  public ResponseEntity<GetUserCouponsResponse> getUserCouponsByUserId(
      @PageableDefault
      @SortDefault.SortDefaults({
          @SortDefault(sort = "expiresAt", direction = Sort.Direction.ASC),
          @SortDefault(sort = "createdAt", direction = Sort.Direction.DESC)
      }) Pageable pageable,
      @CurrentUserInfo CurrentUserInfoDto userInfoDto
  ) {
    PageResponse<GetUserCouponInfo> userCouponInfos =
        userCouponService.getUserCouponsByUserId(userInfoDto, pageable);
    return ResponseEntity.ok()
        .body(GetUserCouponsResponse.from(userCouponInfos));
  }
}
