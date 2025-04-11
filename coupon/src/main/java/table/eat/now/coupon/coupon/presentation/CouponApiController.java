package table.eat.now.coupon.coupon.presentation;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import table.eat.now.common.aop.annotation.AuthCheck;
import table.eat.now.common.resolver.annotation.CurrentUserInfo;
import table.eat.now.common.resolver.dto.CurrentUserInfoDto;
import table.eat.now.common.resolver.dto.UserRole;
import table.eat.now.coupon.coupon.application.dto.response.AvailableCouponInfo;
import table.eat.now.coupon.coupon.application.dto.response.PageResponse;
import table.eat.now.coupon.coupon.application.service.CouponService;
import table.eat.now.coupon.coupon.presentation.dto.response.AvailableCouponsResponse;

@RequiredArgsConstructor
@RequestMapping("/api/v1/coupons")
@RestController
public class CouponApiController {
  private final CouponService couponService;

  @GetMapping("/available")
  public ResponseEntity<AvailableCouponsResponse> getAvailableCoupons(
      @PageableDefault
      @SortDefault.SortDefaults({
          @SortDefault(sort = "endAt", direction = Sort.Direction.ASC),
          @SortDefault(sort = "startAt", direction = Sort.Direction.ASC)
      }) Pageable pageable,
      @RequestParam LocalDateTime time
  ) {

    PageResponse<AvailableCouponInfo> coupons = couponService.getAvailableCoupons(pageable, time);
    return ResponseEntity.ok()
        .body(AvailableCouponsResponse.from(coupons));
  }

  @AuthCheck(roles = {UserRole.CUSTOMER, UserRole.MASTER})
  @PostMapping("/{couponUuid}/issue")
  public ResponseEntity<Void> requestCouponIssue(
      @CurrentUserInfo CurrentUserInfoDto userInfoDto,
      @PathVariable UUID couponUuid
  ) {

    String userCouponUuid = couponService.requestCouponIssue(userInfoDto, couponUuid.toString());
    return ResponseEntity.created(
            ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path("/api/v1/user-coupons/{userCouponUuid}")
                .buildAndExpand(userCouponUuid)
                .toUri())
        .build();
  }
}
