package table.eat.now.coupon.coupon.presentation.dto.response;

import java.time.LocalDateTime;
import lombok.Builder;
import table.eat.now.coupon.coupon.application.dto.response.GetCouponInfo;

@Builder
public record GetCouponResponse(
    Long couponId,
    String couponUuid,
    String name,
    String type,
    LocalDateTime startAt,
    LocalDateTime endAt,
    Integer count,
    Boolean allowDuplicate,
    Integer minPurchaseAmount,
    Integer amount,
    Integer percent,
    Integer maxDiscountAmount,
    LocalDateTime createdAt,
    Long createdBy
) {

  public static GetCouponResponse from(GetCouponInfo coupon) {
    return GetCouponResponse.builder()
        .couponId(coupon.couponId())
        .couponUuid(coupon.couponUuid())
        .name(coupon.name())
        .type(coupon.type())
        .startAt(coupon.startAt())
        .endAt(coupon.endAt())
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
