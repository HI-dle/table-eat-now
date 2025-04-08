package table.eat.now.coupon.coupon.fixture;


import java.time.LocalDateTime;
import table.eat.now.coupon.coupon.domain.entity.Coupon;
import table.eat.now.coupon.coupon.domain.entity.CouponType;
import table.eat.now.coupon.coupon.domain.entity.DiscountPolicy;

public class CouponFixture {

  public static Coupon createCoupon(
      int i, String type, boolean allowDuplicate,
      Integer amount, Integer percent, Integer maxDiscountAmount
  ) {

    Coupon coupon = Coupon.of("test 쿠폰 " + i, CouponType.valueOf(type),
        LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(10), 10000, allowDuplicate);
    DiscountPolicy policy = DiscountPolicy.of(10000, amount, percent, maxDiscountAmount);
    coupon.registerPolicy(policy);
    return coupon;
  }
}
