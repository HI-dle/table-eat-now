package table.eat.now.coupon.coupon.presentation.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import table.eat.now.coupon.coupon.application.dto.response.AvailableCouponInfo;
import table.eat.now.coupon.coupon.application.dto.response.PageResponse;

@Builder
public record AvailableCouponsResponse(
    List<AvailableCouponResponse> coupons,
    long totalElements,
    int totalPages,
    int pageNumber,
    int pageSize
) {

  public static AvailableCouponsResponse from(PageResponse<AvailableCouponInfo> coupons) {
    return AvailableCouponsResponse.builder()
        .coupons(coupons.contents().stream().map(AvailableCouponResponse::from).toList())
        .totalElements(coupons.totalElements())
        .totalPages(coupons.totalPages())
        .pageNumber(coupons.pageNumber())
        .pageSize(coupons.pageSize())
        .build();
  }
  @Builder
  public record AvailableCouponResponse(
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
      Long createdBy
  ) {

    public static AvailableCouponResponse from(
        AvailableCouponInfo coupon) {
      return AvailableCouponResponse.builder()
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
          .build();
    }
  }
}
