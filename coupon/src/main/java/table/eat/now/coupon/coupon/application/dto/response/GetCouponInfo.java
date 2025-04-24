package table.eat.now.coupon.coupon.application.dto.response;

import java.time.LocalDateTime;
import lombok.Builder;
import table.eat.now.coupon.coupon.domain.entity.Coupon;

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
    Boolean allowDuplicate,
    Integer minPurchaseAmount,
    Integer amount,
    Integer percent,
    Integer maxDiscountAmount,
    LocalDateTime createdAt,
    Long createdBy
) {

  public static GetCouponInfo from(Coupon coupon) {
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
        .allowDuplicate(coupon.getAllowDuplicate())
        .minPurchaseAmount(coupon.getDiscountPolicy().getMinPurchaseAmount())
        .amount(coupon.getDiscountPolicy().getAmount())
        .percent(coupon.getDiscountPolicy().getPercent())
        .maxDiscountAmount(coupon.getDiscountPolicy().getMaxDiscountAmount())
        .createdAt(coupon.getCreatedAt())
        .createdBy(coupon.getCreatedBy())
        .build();
  }
}
