package table.eat.now.coupon.coupon.domain.entity;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum CouponType {
  FIXED_DISCOUNT("정액 할인"),
  PERCENT_DISCOUNT("정률 할인"),
  ;

  private final String description;

  public static CouponType parse(String type) {

    try {
      return CouponType.valueOf(type.toUpperCase());
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException("지원하지 않는 쿠폰 타입입니다: " + type);
    }
  }
}
