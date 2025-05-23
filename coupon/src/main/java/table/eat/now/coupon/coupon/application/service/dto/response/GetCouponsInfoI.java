package table.eat.now.coupon.coupon.application.service.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import lombok.Builder;
import table.eat.now.coupon.coupon.domain.entity.Coupon;

@Builder
public record GetCouponsInfoI(
    List<GetCouponInfoI> coupons
) {

  public static GetCouponsInfoI from(List<Coupon> coupons) {
    return GetCouponsInfoI.builder()
        .coupons(coupons.stream()
            .map(GetCouponInfoI::from)
            .toList())
        .build();
  }

  public static GetCouponsInfoI from(Map<String, Coupon> couponMap) {
    return GetCouponsInfoI.builder()
        .coupons(couponMap.values().stream()
            .map(GetCouponInfoI::from)
            .toList())
        .build();
  }

  @Builder
  public record GetCouponInfoI(
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
      Long createdBy,
      Long version
  ) {

    public static GetCouponInfoI from(
        Coupon coupon) {
      return GetCouponInfoI.builder()
          .couponId(coupon.getId())
          .couponUuid(coupon.getCouponUuid())
          .name(coupon.getName())
          .type(coupon.getType().toString())
          .label(coupon.getLabel().toString())
          .issueStartAt(coupon.getPeriod().getIssueStartAt())
          .issueEndAt(coupon.getPeriod().getIssueEndAt())
          .expireAt(coupon.getPeriod().getExpireAt())
          .validDays(coupon.getPeriod().getValidDays())
          .count(coupon.getCount())
          .allowDuplicate(coupon.getAllowDuplicate())
          .minPurchaseAmount(coupon.getDiscountPolicy().getMinPurchaseAmount())
          .amount(coupon.getDiscountPolicy().getAmount())
          .percent(coupon.getDiscountPolicy().getPercent())
          .maxDiscountAmount(coupon.getDiscountPolicy().getMaxDiscountAmount())
          .createdAt(coupon.getCreatedAt())
          .createdBy(coupon.getCreatedBy())
          .version(coupon.getVersion())
          .build();
    }
  }
}
