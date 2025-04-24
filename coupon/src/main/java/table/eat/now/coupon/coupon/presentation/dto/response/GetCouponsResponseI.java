package table.eat.now.coupon.coupon.presentation.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import table.eat.now.coupon.coupon.application.dto.response.GetCouponsInfoI;
import table.eat.now.coupon.coupon.application.dto.response.GetCouponsInfoI.GetCouponInfoI;

@Builder
public record GetCouponsResponseI(
    List<GetCouponResponseI> coupons
) {

  public static GetCouponsResponseI from(GetCouponsInfoI coupons) {
    return GetCouponsResponseI.builder()
        .coupons(coupons.coupons()
            .stream()
            .map(GetCouponResponseI::from)
            .toList())
        .build();
  }

  @Builder
  public record GetCouponResponseI(
      Long couponId,
      String couponUuid,
      String name,
      String type,
      String label,
      LocalDateTime startAt,
      LocalDateTime endAt,
      Integer validDays,
      Integer count,
      Boolean allowDuplicate,
      Integer minPurchaseAmount,
      Integer amount,
      Integer percent,
      Integer maxDiscountAmount,
      LocalDateTime createdAt,
      Long createdBy
  ) {

    public static GetCouponResponseI from(
        GetCouponInfoI coupon) {
      return GetCouponResponseI.builder()
          .couponId(coupon.couponId())
          .couponUuid(coupon.couponUuid())
          .name(coupon.name())
          .type(coupon.type())
          .label(coupon.label())
          .startAt(coupon.startAt())
          .endAt(coupon.endAt())
          .validDays(coupon.validDays())
          .count(coupon.count())
          .allowDuplicate(coupon.allowDuplicate())
          .minPurchaseAmount(coupon.minPurchaseAmount())
          .amount(coupon.amount())
          .percent(coupon.percent())
          .maxDiscountAmount(coupon.maxDiscountAmount())
          .createdAt(coupon.createdAt())
          .createdBy(coupon.createdBy())
          .build();
    }
  }
}
