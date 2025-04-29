package table.eat.now.coupon.coupon.presentation.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import table.eat.now.coupon.coupon.application.service.dto.response.PageResponse;
import table.eat.now.coupon.coupon.application.service.dto.response.SearchCouponInfo;

@Builder
public record SearchCouponsResponse(
    List<SearchCouponResponse> coupons,
    long totalElements,
    int totalPages,
    int pageNumber,
    int pageSize
) {

  public static SearchCouponsResponse from(PageResponse<SearchCouponInfo> coupons) {
    return SearchCouponsResponse.builder()
        .coupons(coupons.contents().stream().map(SearchCouponResponse::from).toList())
        .totalElements(coupons.totalElements())
        .totalPages(coupons.totalPages())
        .pageNumber(coupons.pageNumber())
        .pageSize(coupons.pageSize())
        .build();
  }

  @Builder
  public record SearchCouponResponse(
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

    public static SearchCouponResponse from(SearchCouponInfo searchCouponInfo) {
      return SearchCouponResponse.builder()
          .couponId(searchCouponInfo.couponId())
          .couponUuid(searchCouponInfo.couponUuid())
          .name(searchCouponInfo.name())
          .type(searchCouponInfo.type())
          .label(searchCouponInfo.label())
          .issueStartAt(searchCouponInfo.issueStartAt())
          .issueEndAt(searchCouponInfo.issueEndAt())
          .expireAt(searchCouponInfo.expireAt())
          .validDays(searchCouponInfo.validDays())
          .count(searchCouponInfo.count())
          .allowDuplicate(searchCouponInfo.allowDuplicate())
          .minPurchaseAmount(searchCouponInfo.minPurchaseAmount())
          .amount(searchCouponInfo.amount())
          .percent(searchCouponInfo.percent())
          .maxDiscountAmount(searchCouponInfo.maxDiscountAmount())
          .createdAt(searchCouponInfo.createdAt())
          .createdBy(searchCouponInfo.createdBy())
          .version(searchCouponInfo.version())
          .build();
    }
  }
}
