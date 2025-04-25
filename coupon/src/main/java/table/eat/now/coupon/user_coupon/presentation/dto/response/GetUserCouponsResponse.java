package table.eat.now.coupon.user_coupon.presentation.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import table.eat.now.coupon.user_coupon.application.dto.response.PageResponse;
import table.eat.now.coupon.user_coupon.application.dto.response.GetUserCouponInfo;

@Builder
public record GetUserCouponsResponse(
    List<GetUserCouponResponse> userCoupons,
    long totalElements,
    int totalPages,
    int pageNumber,
    int pageSize
) {

  public static GetUserCouponsResponse from(PageResponse<GetUserCouponInfo> userCouponInfos) {
    return GetUserCouponsResponse.builder()
        .userCoupons(userCouponInfos.contents().stream().map(GetUserCouponResponse::from).toList())
        .totalElements(userCouponInfos.totalElements())
        .totalPages(userCouponInfos.totalPages())
        .pageNumber(userCouponInfos.pageNumber())
        .pageSize(userCouponInfos.pageSize())
        .build();
  }

  @Builder
  record GetUserCouponResponse(
      Long id,
      String userCouponUuid,
      String couponUuid,
      Long userId,
      String reservationUuid,
      String name,
      String status,
      LocalDateTime expiresAt,
      LocalDateTime preemptAt,
      LocalDateTime usedAt,
      LocalDateTime createdAt,
      Long createdBy
  ) {

    public static GetUserCouponResponse from(GetUserCouponInfo getUserCouponInfo) {
      return GetUserCouponResponse.builder()
          .id(getUserCouponInfo.id())
          .userCouponUuid(getUserCouponInfo.userCouponUuid())
          .couponUuid(getUserCouponInfo.couponUuid())
          .userId(getUserCouponInfo.userId())
          .reservationUuid(getUserCouponInfo.reservationUuid())
          .name(getUserCouponInfo.name())
          .status(getUserCouponInfo.status())
          .expiresAt(getUserCouponInfo.expiresAt())
          .preemptAt(getUserCouponInfo.preemptAt())
          .usedAt(getUserCouponInfo.usedAt())
          .createdAt(getUserCouponInfo.createdAt())
          .createdBy(getUserCouponInfo.createdBy())
          .build();
    }
  }
}
