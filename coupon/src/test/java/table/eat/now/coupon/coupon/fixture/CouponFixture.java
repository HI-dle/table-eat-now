package table.eat.now.coupon.coupon.fixture;


import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;
import table.eat.now.coupon.coupon.application.dto.response.AvailableCouponInfo;
import table.eat.now.coupon.coupon.application.dto.response.SearchCouponInfo;
import table.eat.now.coupon.coupon.domain.entity.Coupon;
import table.eat.now.coupon.coupon.domain.entity.DiscountPolicy;

public class CouponFixture {

  public static List<Coupon> createCoupons(int i) {
    if (i <= 0) return null;

    return IntStream.range(0, i)
        .mapToObj(j -> CouponFixture.createCoupon(
            j, "FIXED_DISCOUNT", "HOT",1000 * i ,false,
            1000, null, null)
        )
        .toList();
  }

  public static Coupon createCoupon(
      int i, String type, String label, Integer count, boolean allowDuplicate,
      Integer amount, Integer percent, Integer maxDiscountAmount
  ) {

    Coupon coupon = Coupon.of("test 쿠폰 " + i, type, label,
        LocalDateTime.now().plusDays(2+i).truncatedTo(ChronoUnit.DAYS),
        LocalDateTime.now().plusDays(12+i).truncatedTo(ChronoUnit.DAYS),
        7, count, allowDuplicate);
    DiscountPolicy policy = DiscountPolicy.of(
        10000, amount, percent, maxDiscountAmount);
    coupon.registerPolicy(policy);
    return coupon;
  }

  public static List<SearchCouponInfo> createCouponInfos(int length) {
    List<SearchCouponInfo> couponInfos = IntStream.range(0, length)
        .mapToObj(i -> SearchCouponInfo.builder()
            .couponId((long) i)
            .couponUuid(UUID.randomUUID().toString())
            .name("test coupon " + i)
            .type("FIXED_DISCOUNT")
            .label("HOT")
            .startAt(LocalDateTime.now().minusDays(i).truncatedTo(ChronoUnit.DAYS))
            .endAt(LocalDateTime.now().plusDays(19-i).truncatedTo(ChronoUnit.DAYS))
            .validDays(7)
            .count(10000 * i)
            .allowDuplicate(false)
            .minPurchaseAmount(50000)
            .amount(3000)
            .percent(null)
            .maxDiscountAmount(null)
            .createdAt(LocalDateTime.now().minusHours(i))
            .createdBy(1L)
            .build()
        )
        .toList();
    return couponInfos;
  }

  public static List<AvailableCouponInfo> createAvailableCouponInfos(int length) {
    List<AvailableCouponInfo> couponInfos = IntStream.range(0, length)
        .mapToObj(i -> AvailableCouponInfo.builder()
            .couponId((long) i)
            .couponUuid(UUID.randomUUID().toString())
            .name("test coupon " + i)
            .type("FIXED_DISCOUNT")
            .label("HOT")
            .startAt(LocalDateTime.now().minusDays(i).truncatedTo(ChronoUnit.DAYS))
            .endAt(LocalDateTime.now().plusDays(19-i).truncatedTo(ChronoUnit.DAYS))
            .validDays(7)
            .count(10000 * i)
            .issuedCount(100)
            .allowDuplicate(false)
            .minPurchaseAmount(50000)
            .amount(3000)
            .percent(null)
            .maxDiscountAmount(null)
            .createdAt(LocalDateTime.now().minusHours(i))
            .createdBy(1L)
            .build()
        )
        .toList();
    return couponInfos;
  }
}
