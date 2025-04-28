package table.eat.now.coupon.coupon.presentation.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import table.eat.now.coupon.coupon.application.service.dto.response.IssuableCouponInfo;
import table.eat.now.coupon.coupon.application.service.dto.response.PageResponse;

@Builder
public record IssuableCouponsResponse(
    List<IssuableCouponResponse> coupons,
    long totalElements,
    int totalPages,
    int pageNumber,
    int pageSize
) {

  public static IssuableCouponsResponse from(PageResponse<IssuableCouponInfo> coupons) {
    return IssuableCouponsResponse.builder()
        .coupons(coupons.contents().stream().map(IssuableCouponResponse::from).toList())
        .totalElements(coupons.totalElements())
        .totalPages(coupons.totalPages())
        .pageNumber(coupons.pageNumber())
        .pageSize(coupons.pageSize())
        .build();
  }
  @Builder
  public record IssuableCouponResponse(
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

    public static IssuableCouponResponse from(
        IssuableCouponInfo coupon) {
      return IssuableCouponResponse.builder()
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
