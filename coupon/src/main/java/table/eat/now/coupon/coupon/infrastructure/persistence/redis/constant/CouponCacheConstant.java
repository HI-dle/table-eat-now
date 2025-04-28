package table.eat.now.coupon.coupon.infrastructure.persistence.redis.constant;

public class CouponCacheConstant {
  private CouponCacheConstant() {
    throw new IllegalStateException("Constant class");
  }

  public static final String COUPON_CACHE = "coupon:";
  public static final String COUPON_USER_SET = "coupon:user:";

  public static final String DAILY_ISSUABLE_PROMO_COUPON_INDEX = "coupon:iss:promo:index";
  public static final String DAILY_ISSUABLE_HOT_COUPON_INDEX = "coupon:iss:hot:index";
  //public static final String ISSUABLE_POPULAR_COUPON_INDEX = "coupon:popular:index";

  public static final int DAILY_COUPON_TTL_HR = 28;
}
