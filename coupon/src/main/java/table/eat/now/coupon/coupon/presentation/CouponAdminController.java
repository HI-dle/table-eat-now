package table.eat.now.coupon.coupon.presentation;

import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;
import table.eat.now.common.aop.annotation.AuthCheck;
import table.eat.now.common.resolver.annotation.CurrentUserInfo;
import table.eat.now.common.resolver.dto.CurrentUserInfoDto;
import table.eat.now.common.resolver.dto.UserRole;
import table.eat.now.coupon.coupon.application.service.CouponService;
import table.eat.now.coupon.coupon.presentation.dto.request.CreateCouponRequest;

@RequiredArgsConstructor
@RequestMapping("/admin/v1/coupons")
@RestController
public class CouponAdminController {

  private final CouponService couponService;

  @AuthCheck(roles = {UserRole.MASTER})
  @PostMapping
  public ResponseEntity<Void> createCoupon(@RequestBody @Valid CreateCouponRequest request,
      @CurrentUserInfo CurrentUserInfoDto userInfo) {

    UUID couponUuid = couponService.createCoupon(request.toCommand());
    return ResponseEntity.created(
        UriComponentsBuilder.fromUriString("/admin/v1/coupons/{couponUuid}")
        .buildAndExpand(couponUuid)
        .toUri())
        .build();
  }

}
