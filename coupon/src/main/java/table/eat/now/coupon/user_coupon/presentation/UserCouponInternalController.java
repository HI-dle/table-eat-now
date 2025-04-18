package table.eat.now.coupon.user_coupon.presentation;

import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import table.eat.now.common.aop.annotation.AuthCheck;
import table.eat.now.common.resolver.annotation.CurrentUserInfo;
import table.eat.now.common.resolver.dto.CurrentUserInfoDto;
import table.eat.now.common.resolver.dto.UserRole;
import table.eat.now.coupon.user_coupon.application.service.UserCouponService;
import table.eat.now.coupon.user_coupon.presentation.dto.request.PreemptUserCouponRequest;

@RequiredArgsConstructor
@RequestMapping("/internal/v1/user-coupons")
@RestController
public class UserCouponInternalController {
  private final UserCouponService userCouponService;

  @AuthCheck(roles = {UserRole.CUSTOMER, UserRole.MASTER})
  @PatchMapping("/{userCouponUuid}/preempt")
  public ResponseEntity<Void> preemptUserCoupon(
      @CurrentUserInfo CurrentUserInfoDto userInfoDto,
      @PathVariable UUID userCouponUuid,
      @Valid @RequestBody PreemptUserCouponRequest request
  ) {

    userCouponService.preemptUserCoupon(userInfoDto, userCouponUuid.toString(), request.toCommand());
    return ResponseEntity.ok().build();
  }
}
