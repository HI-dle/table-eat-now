/**
 * @author : jieun(je-pa)
 * @Date : 2025. 04. 11.
 */
package table.eat.now.reservation.reservation.application.client.dto.response;

import java.time.LocalDateTime;
import java.util.Map;
import lombok.Builder;

@Builder
public record GetCouponsInfo(
    Map<String, Coupon> couponMap
) {

  @Builder
  public record Coupon(
      String couponUuid,
      CouponType type,
      LocalDateTime startAt,
      LocalDateTime endAt,
      Integer minPurchaseAmount,
      Integer amount,
      Integer percent,
      Integer maxDiscountAmount
  ){
    public enum CouponType {
      PERCENT_DISCOUNT,
      FIXED_DISCOUNT
    }
  }
}
