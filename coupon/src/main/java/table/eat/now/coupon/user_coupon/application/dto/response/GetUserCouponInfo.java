package table.eat.now.coupon.user_coupon.application.dto.response;

import java.time.LocalDateTime;
import lombok.Builder;
import table.eat.now.coupon.user_coupon.domain.entity.UserCoupon;

@Builder
public record GetUserCouponInfo(
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

  public static GetUserCouponInfo from(UserCoupon userCoupon) {
    return GetUserCouponInfo.builder()
        .id(userCoupon.getId())
        .userCouponUuid(userCoupon.getUserCouponUuid())
        .couponUuid(userCoupon.getCouponUuid())
        .userId(userCoupon.getUserId())
        .reservationUuid(userCoupon.getReservationUuid())
        .name(userCoupon.getName())
        .status(userCoupon.getStatus().toString())
        .expiresAt(userCoupon.getExpiresAt())
        .preemptAt(userCoupon.getPreemptAt())
        .usedAt(userCoupon.getUsedAt())
        .createdAt(userCoupon.getCreatedAt())
        .createdBy(userCoupon.getCreatedBy())
        .build();
  }
}
