/**
 * @author : jieun(je-pa)
 * @Date : 2025. 04. 11.
 */
package table.eat.now.reservation.reservation.infrastructure.client.feign.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Builder;
import table.eat.now.reservation.reservation.application.client.dto.response.GetCouponsInfo;
import table.eat.now.reservation.reservation.application.client.dto.response.GetCouponsInfo.Coupon.CouponType;

public record GetCouponsResponse(
    List<Coupon> coupons
) {

  public GetCouponsInfo toInfo() {
    return GetCouponsInfo.builder()
        .couponMap(coupons.stream()
            .collect(Collectors.toMap(
                GetCouponsResponse.Coupon::couponUuid, GetCouponsResponse.Coupon::toInfoCoupon)))
        .build();
  }

  @Builder
  private record Coupon(
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
  ){

    public GetCouponsInfo.Coupon toInfoCoupon() {
      return GetCouponsInfo.Coupon.builder()
          .couponUuid(couponUuid)
          .type(CouponType.valueOf(type))
          .startAt(startAt)
          .endAt(endAt)
          .minPurchaseAmount(minPurchaseAmount)
          .amount(amount)
          .percent(percent)
          .maxDiscountAmount(maxDiscountAmount)
          .build();
    }
  }
}
