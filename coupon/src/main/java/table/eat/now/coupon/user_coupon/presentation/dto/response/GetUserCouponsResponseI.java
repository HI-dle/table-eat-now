package table.eat.now.coupon.user_coupon.presentation.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import table.eat.now.coupon.user_coupon.application.client.dto.response.GetCouponInfoI;
import table.eat.now.coupon.user_coupon.application.dto.response.GetUserCouponInfoI;

@Builder
public record GetUserCouponsResponseI(
    List<GetUserCouponResponseI> userCoupons
) {

  public static GetUserCouponsResponseI from(List<GetUserCouponInfoI> userCouponInfos) {
    return GetUserCouponsResponseI.builder()
        .userCoupons(userCouponInfos.stream()
            .map(GetUserCouponResponseI::from)
            .toList())
        .build();
  }

  @Builder
  record GetUserCouponResponseI(
      Long id,
      String userCouponUuid,
      String couponUuid,
      GetCouponResponseI coupon,
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

    public static GetUserCouponResponseI from(
        GetUserCouponInfoI infoI) {
      return GetUserCouponResponseI.builder()
          .id(infoI.id())
          .userCouponUuid(infoI.userCouponUuid())
          .couponUuid(infoI.couponUuid())
          .coupon(GetCouponResponseI.from(infoI.coupon()))
          .userId(infoI.userId())
          .reservationUuid(infoI.reservationUuid())
          .name(infoI.name())
          .status(infoI.status())
          .expiresAt(infoI.expiresAt())
          .preemptAt(infoI.preemptAt())
          .usedAt(infoI.usedAt())
          .createdAt(infoI.createdAt())
          .createdBy(infoI.createdBy())
          .build();
    }
  }

  @Builder
  record GetCouponResponseI(
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

    public static GetCouponResponseI from(GetCouponInfoI coupon) {
      if (coupon == null) {
        return null;
      }
      return GetCouponResponseI.builder()
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
