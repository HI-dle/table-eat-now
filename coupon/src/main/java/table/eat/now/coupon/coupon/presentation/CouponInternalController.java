package table.eat.now.coupon.coupon.presentation;

import jakarta.validation.constraints.NotEmpty;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import table.eat.now.common.aop.annotation.AuthCheck;
import table.eat.now.coupon.coupon.application.service.dto.response.GetCouponInfo;
import table.eat.now.coupon.coupon.application.service.dto.response.GetCouponsInfoI;
import table.eat.now.coupon.coupon.application.service.CouponService;
import table.eat.now.coupon.coupon.presentation.dto.response.GetCouponResponse;
import table.eat.now.coupon.coupon.presentation.dto.response.GetCouponsResponseI;

@RequiredArgsConstructor
@RequestMapping("/internal/v1/coupons")
@RestController
public class CouponInternalController {
  private final CouponService couponService;

  @AuthCheck
  @GetMapping("/{couponUuid}")
  public ResponseEntity<GetCouponResponse> getCouponInternal(
      @PathVariable UUID couponUuid
  ) {

    GetCouponInfo coupon = couponService.getCouponInfo(couponUuid.toString());
    return ResponseEntity.ok()
        .body(GetCouponResponse.from(coupon));
  }

  @AuthCheck
  @GetMapping
  public ResponseEntity<GetCouponsResponseI> getCouponsInternal(
      @RequestParam @NotEmpty Set<UUID> couponUuids
  ) {

    GetCouponsInfoI coupons = couponService.getCouponsInternal(couponUuids);
    return ResponseEntity.ok()
        .body(GetCouponsResponseI.from(coupons));
  }
}
