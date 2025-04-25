package table.eat.now.coupon.coupon.presentation.dto.response;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.Builder;
import table.eat.now.coupon.coupon.application.dto.response.GetCouponsInfoI;
import table.eat.now.coupon.coupon.application.dto.response.GetCouponsInfoI.GetCouponInfoI;

@Builder
public record GetCouponsResponseI(
    Map<String, GetCouponResponseI> coupons
) {

  public static GetCouponsResponseI from(GetCouponsInfoI coupons) {
    return GetCouponsResponseI.builder()
        .coupons(coupons.coupons()
            .stream()
            .map(GetCouponResponseI::from)
            .collect(Collectors.toMap(
                GetCouponResponseI::couponUuid,
                Function.identity()
            )))
        .build();
  }


  @Builder
  public record GetCouponResponseI(
      Long couponId,
      String couponUuid,
      String name,
      String type,
      String label,
      LocalDateTime issueStartAt,
      LocalDateTime issueEndAt,
      LocalDateTime expireAt,
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
          .issueStartAt(coupon.issueStartAt())
          .issueEndAt(coupon.issueEndAt())
          .expireAt(coupon.expireAt())
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
