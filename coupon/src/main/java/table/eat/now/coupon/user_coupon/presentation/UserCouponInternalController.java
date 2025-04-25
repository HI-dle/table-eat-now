package table.eat.now.coupon.user_coupon.presentation;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import table.eat.now.common.aop.annotation.AuthCheck;
import table.eat.now.common.resolver.annotation.CurrentUserInfo;
import table.eat.now.common.resolver.dto.CurrentUserInfoDto;
import table.eat.now.common.resolver.dto.UserRole;
import table.eat.now.coupon.user_coupon.application.dto.response.GetUserCouponInfoI;
import table.eat.now.coupon.user_coupon.application.service.UserCouponService;
import table.eat.now.coupon.user_coupon.presentation.dto.request.PreemptUserCouponRequest;
import table.eat.now.coupon.user_coupon.presentation.dto.response.GetUserCouponsResponseI;

@RequiredArgsConstructor
@RequestMapping("/internal/v1/user-coupons")
@RestController
public class UserCouponInternalController {
  private final UserCouponService userCouponService;

  @AuthCheck(roles = {UserRole.CUSTOMER, UserRole.MASTER})
  @PatchMapping("/preempt")
  public ResponseEntity<Void> preemptUserCoupons(
      @CurrentUserInfo CurrentUserInfoDto userInfoDto,
      @RequestBody @Valid PreemptUserCouponRequest request
  ) {

    userCouponService.preemptUserCoupons(userInfoDto, request.toCommand());
    return ResponseEntity.ok().build();
  }

  @AuthCheck(roles = {UserRole.CUSTOMER, UserRole.MASTER})
  @GetMapping
  public ResponseEntity<GetUserCouponsResponseI> getUserCoupons(
      @CurrentUserInfo CurrentUserInfoDto userInfoDto,
      @RequestParam Set<UUID> userCouponUuids
  ) {

    List<GetUserCouponInfoI> userCouponsInfo =
        userCouponService.getUserCouponsInternalBy(userCouponUuids.stream()
            .map(UUID::toString)
            .collect(Collectors.toSet()));
    return ResponseEntity.ok().body(GetUserCouponsResponseI.from(userCouponsInfo));
  }
}
