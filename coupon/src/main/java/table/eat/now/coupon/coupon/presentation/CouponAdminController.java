package table.eat.now.coupon.coupon.presentation;

import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import table.eat.now.common.aop.annotation.AuthCheck;
import table.eat.now.common.resolver.annotation.CurrentUserInfo;
import table.eat.now.common.resolver.dto.CurrentUserInfoDto;
import table.eat.now.common.resolver.dto.UserRole;
import table.eat.now.coupon.coupon.application.service.dto.response.GetCouponInfo;
import table.eat.now.coupon.coupon.application.service.dto.response.PageResponse;
import table.eat.now.coupon.coupon.application.service.dto.response.SearchCouponInfo;
import table.eat.now.coupon.coupon.application.service.CouponService;
import table.eat.now.coupon.coupon.presentation.dto.request.CreateCouponRequest;
import table.eat.now.coupon.coupon.presentation.dto.request.SearchCouponsRequest;
import table.eat.now.coupon.coupon.presentation.dto.request.UpdateCouponRequest;
import table.eat.now.coupon.coupon.presentation.dto.response.GetCouponResponse;
import table.eat.now.coupon.coupon.presentation.dto.response.SearchCouponsResponse;
import table.eat.now.coupon.coupon.presentation.dto.response.UpdateCouponResponse;

@RequiredArgsConstructor
@RequestMapping("/admin/v1/coupons")
@RestController
public class CouponAdminController {
  private final CouponService couponService;

  @AuthCheck(roles = {UserRole.MASTER})
  @PostMapping
  public ResponseEntity<Void> createCoupon(
      @RequestBody @Valid CreateCouponRequest request,
      @CurrentUserInfo CurrentUserInfoDto userInfo) {

    String couponUuid = couponService.createCoupon(request.toCommand());
    return ResponseEntity.created(
        ServletUriComponentsBuilder
            .fromCurrentRequestUri()
            .path("/{couponUuid}")
            .buildAndExpand(couponUuid)
            .toUri())
            .build();
  }

  @AuthCheck(roles = {UserRole.MASTER})
  @PatchMapping("/{couponUuid}")
  public ResponseEntity<UpdateCouponResponse> updateCoupon(
      @CurrentUserInfo CurrentUserInfoDto userInfo,
      @PathVariable UUID couponUuid,
      @RequestBody @Valid UpdateCouponRequest request
  ) {

    couponService.updateCoupon(couponUuid.toString(), request.toCommand());
    return ResponseEntity.ok()
        .body(UpdateCouponResponse.of(couponUuid));
  }

  @AuthCheck(roles = {UserRole.MASTER, UserRole.OWNER, UserRole.STAFF})
  @GetMapping("/{couponUuid}")
  public ResponseEntity<GetCouponResponse> getCoupon(
      @CurrentUserInfo CurrentUserInfoDto userInfo,
      @PathVariable UUID couponUuid
  ) {

    GetCouponInfo coupon = couponService.getCouponInfo(couponUuid.toString());
    return ResponseEntity.ok()
        .body(GetCouponResponse.from(coupon));
  }

  @AuthCheck(roles = {UserRole.MASTER})
  @DeleteMapping("/{couponUuid}")
  public ResponseEntity<Void> deleteCoupon(
      @CurrentUserInfo CurrentUserInfoDto userInfo,
      @PathVariable UUID couponUuid
  ) {

    couponService.deleteCoupon(userInfo, couponUuid.toString());
    return ResponseEntity.noContent().build();
  }

  @AuthCheck(roles = {UserRole.MASTER})
  @GetMapping
  public ResponseEntity<SearchCouponsResponse> searchCoupons(
      @CurrentUserInfo CurrentUserInfoDto userInfo,
      @PageableDefault
      @SortDefault.SortDefaults({
          @SortDefault(sort = "issueEndAt", direction = Sort.Direction.ASC),
          @SortDefault(sort = "issueStartAt", direction = Sort.Direction.ASC)
      }) Pageable pageable,
      SearchCouponsRequest request
  ) {

    PageResponse<SearchCouponInfo> coupons = couponService.searchCoupons(pageable, request.toQuery());
    return ResponseEntity.ok()
        .body(SearchCouponsResponse.from(coupons));
  }
}
