package table.eat.now.coupon.user_coupon.application.dto.response;

import java.time.LocalDateTime;
import lombok.Builder;
import table.eat.now.coupon.user_coupon.application.client.dto.response.GetCouponInfoI;
import table.eat.now.coupon.user_coupon.domain.entity.UserCoupon;

@Builder
public record GetUserCouponInfoI(
    Long id,
    String userCouponUuid,
    String couponUuid,
    GetCouponInfoI coupon,
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

  public static GetUserCouponInfoI from(UserCoupon userCoupon, GetCouponInfoI getCouponInfoI) {
    return GetUserCouponInfoI.builder()
        .id(userCoupon.getId())
        .userCouponUuid(userCoupon.getUserCouponUuid())
        .couponUuid(userCoupon.getCouponUuid())
        .coupon(getCouponInfoI)
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
