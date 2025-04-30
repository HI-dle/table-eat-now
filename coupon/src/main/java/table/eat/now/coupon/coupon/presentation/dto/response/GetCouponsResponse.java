package table.eat.now.coupon.coupon.presentation.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import table.eat.now.coupon.coupon.application.service.dto.response.GetCouponsInfo;

@Builder
public record GetCouponsResponse(
    List<GetCouponResponse> coupons
) {

  public static GetCouponsResponse from(GetCouponsInfo coupons) {
    return GetCouponsResponse.builder()
        .coupons(coupons.coupons()
            .stream()
            .map(GetCouponResponse::from).toList()
        )
        .build();
  }
  @Builder
  public record GetCouponResponse(
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

    public static GetCouponResponse from(
        GetCouponsInfo.GetCouponInfo coupon) {
      return GetCouponResponse.builder()
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
          .issuedCount(coupon.issuedCount())
          .allowDuplicate(coupon.allowDuplicate())
          .minPurchaseAmount(coupon.minPurchaseAmount())
          .amount(coupon.amount())
          .percent(coupon.percent())
          .maxDiscountAmount(coupon.maxDiscountAmount())
          .createdAt(coupon.createdAt())
          .createdBy(coupon.createdBy())
          .version(coupon.version())
          .build();
    }
  }
}
