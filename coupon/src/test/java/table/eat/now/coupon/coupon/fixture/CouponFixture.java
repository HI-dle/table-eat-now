package table.eat.now.coupon.coupon.fixture;


import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.IntStream;
import table.eat.now.coupon.coupon.domain.entity.Coupon;
import table.eat.now.coupon.coupon.domain.entity.CouponType;
import table.eat.now.coupon.coupon.domain.entity.DiscountPolicy;

public class CouponFixture {

  public static List<Coupon> createCoupons(int i) {
    if (i <= 0) return null;

    return IntStream.range(0, i)
        .mapToObj(j -> CouponFixture.createCoupon(
            j, "FIXED_DISCOUNT", false,
            1000, null, null)
        )
        .toList();
  }

  public static Coupon createCoupon(
      int i, String type, boolean allowDuplicate,
      Integer amount, Integer percent, Integer maxDiscountAmount
  ) {

    Coupon coupon = Coupon.of("test 쿠폰 " + i, CouponType.valueOf(type),
        LocalDateTime.now().plusDays(1+i).truncatedTo(ChronoUnit.DAYS),
        LocalDateTime.now().plusDays(11+i).truncatedTo(ChronoUnit.DAYS),
        10000 * i, allowDuplicate);
    DiscountPolicy policy = DiscountPolicy.of(
        10000, amount, percent, maxDiscountAmount);
    coupon.registerPolicy(policy);
    return coupon;
  }
}
