package table.eat.now.coupon.coupon.application.dto.response;

import java.time.LocalDateTime;
import lombok.Builder;
import table.eat.now.coupon.coupon.domain.entity.Coupon;

@Builder
public record AvailableCouponInfo(
    Long couponId,
    String couponUuid,
    String name,
    String type,
    LocalDateTime startAt,
    LocalDateTime endAt,
    Integer count,
    Integer issuedCount,
    Boolean allowDuplicate,
    Integer minPurchaseAmount,
    Integer amount,
    Integer percent,
    Integer maxDiscountAmount,
    LocalDateTime createdAt,
    Long createdBy
) {

  public static AvailableCouponInfo from(Coupon coupon, Integer remainder) {
    return AvailableCouponInfo.builder()
        .couponId(coupon.getId())
        .couponUuid(coupon.getCouponUuid())
        .name(coupon.getName())
        .type(coupon.getType().toString())
        .startAt(coupon.getPeriod().getStartAt())
        .endAt(coupon.getPeriod().getEndAt())
        .count(coupon.getCount())
        .issuedCount(calcIssuedCount(remainder, coupon))
        .allowDuplicate(coupon.getAllowDuplicate())
        .minPurchaseAmount(coupon.getDiscountPolicy().getMinPurchaseAmount())
        .amount(coupon.getDiscountPolicy().getAmount())
        .percent(coupon.getDiscountPolicy().getPercent())
        .maxDiscountAmount(coupon.getDiscountPolicy().getMaxDiscountAmount())
        .createdAt(coupon.getCreatedAt())
        .createdBy(coupon.getCreatedBy())
        .build();
  }

  private static Integer calcIssuedCount(Integer remainder, Coupon coupon) {
    return remainder == null
        ? null
        : remainder <= 0 ? coupon.getCount() : coupon.getCount() - remainder;
  }
}
