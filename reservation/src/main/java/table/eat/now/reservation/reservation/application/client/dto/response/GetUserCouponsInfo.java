/**
 * @author : jieun(je-pa)
 * @Date : 2025. 04. 11.
 */
package table.eat.now.reservation.reservation.application.client.dto.response;

import java.time.LocalDateTime;
import java.util.Map;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;

@Builder
public record GetUserCouponsInfo(
    Map<String, UserCoupon> userCouponMap
) {

  @Builder
  public record UserCoupon(
      String userCouponUuid,
      Coupon coupon,
      Long userId,
      UserCouponStatus status,
      LocalDateTime expiresAt
  ){
    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    public enum UserCouponStatus {
      ISSUED("발급"),
      PREEMPT("선점"),
      COMMIT("사용완료"),
      ROLLBACK("사용취소"),
      ;
      private final String description;
    }

    @Builder
    public record Coupon(
        CouponType type,
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

}
