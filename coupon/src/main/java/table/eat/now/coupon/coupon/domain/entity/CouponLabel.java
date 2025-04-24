package table.eat.now.coupon.coupon.domain.entity;

public enum CouponLabel {
  GENERAL,
  PROMOTION,
  HOT,
  ;

  public static CouponLabel parse(String label) {
    try {
      return CouponLabel.valueOf(label.toUpperCase());
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException("지원하지 않는 쿠폰 라벨입니다: " + label);
    }
  }
}
