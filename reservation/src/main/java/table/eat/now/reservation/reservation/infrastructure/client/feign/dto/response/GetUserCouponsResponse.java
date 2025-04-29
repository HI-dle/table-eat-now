/**
 * @author : jieun(je-pa)
 * @Date : 2025. 04. 11.
 */
package table.eat.now.reservation.reservation.infrastructure.client.feign.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.Builder;
import table.eat.now.reservation.reservation.application.client.dto.response.GetUserCouponsInfo;

public record GetUserCouponsResponse(
    List<UserCoupon> userCoupons
) {

  public GetUserCouponsInfo toInfo() {
    return GetUserCouponsInfo.builder()
        .userCouponMap(userCoupons.stream()
            .collect(Collectors.toMap(
                UserCoupon::userCouponUuid, UserCoupon::toInfo)))
        .build();
  }

  @Builder
  private record UserCoupon(
      Long id,
      String userCouponUuid,
      String couponUuid,
      Coupon coupon,
      Long userId,
      String reservationUuid,
      String name,
      String status,
      LocalDateTime expiresAt,
      LocalDateTime preemptAt,
      LocalDateTime usedAt,
      LocalDateTime createdAt,
      Long createdBy
  ){

    public GetUserCouponsInfo.UserCoupon toInfo() {
      return GetUserCouponsInfo.UserCoupon.builder()
          .userCouponUuid(userCouponUuid)
          .coupon(coupon.toInfo())
          .userId(userId)
          .status(Optional.ofNullable(status)
              .map(GetUserCouponsInfo.UserCoupon.UserCouponStatus::valueOf)
              .orElse(null))
          .expiresAt(expiresAt)
          .build();
    }

    @Builder
    record Coupon(
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

      public GetUserCouponsInfo.UserCoupon.Coupon toInfo() {
        if (this == null) {
          return null;
        }
        return GetUserCouponsInfo.UserCoupon.Coupon.builder()
            .type(Optional.ofNullable(type)
                .map(GetUserCouponsInfo.UserCoupon.Coupon.CouponType::valueOf)
                .orElse(null))
            .minPurchaseAmount(minPurchaseAmount)
            .amount(amount)
            .percent(percent)
            .maxDiscountAmount(maxDiscountAmount)
            .build();
      }
    }
  }
}
