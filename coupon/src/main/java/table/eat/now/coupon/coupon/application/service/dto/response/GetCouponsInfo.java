package table.eat.now.coupon.coupon.application.service.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import lombok.Builder;
import table.eat.now.coupon.coupon.domain.entity.Coupon;

@Builder
public record GetCouponsInfo(
    List<GetCouponInfo> coupons
) {

  public static GetCouponsInfo from(List<Coupon> coupons) {
    return GetCouponsInfo.builder()
        .coupons(coupons.stream()
            .map(GetCouponInfo::from)
            .toList())
        .build();
  }

  public static GetCouponsInfo from(Map<String, Coupon> couponMap) {
    return GetCouponsInfo.builder()
        .coupons(couponMap.values().stream()
            .map(GetCouponInfo::from)
            .toList())
        .build();
  }

  @Builder
  public record GetCouponInfo(
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
      Integer issuedCount,
      Boolean allowDuplicate,
      Integer minPurchaseAmount,
      Integer amount,
      Integer percent,
      Integer maxDiscountAmount,
      LocalDateTime createdAt,
      Long createdBy,
      Long version
  ) {

    public static GetCouponInfo from(
        Coupon coupon) {
      return GetCouponInfo.builder()
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
          .issuedCount(coupon.getIssuedCount())
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
