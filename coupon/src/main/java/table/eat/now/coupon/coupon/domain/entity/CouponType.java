package table.eat.now.coupon.coupon.domain.entity;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum CouponType {
  FIXED_DISCOUNT("정액 할인"),
  PERCENT_DISCOUNT("정률 할인"),
  ;

  private final String description;
}
