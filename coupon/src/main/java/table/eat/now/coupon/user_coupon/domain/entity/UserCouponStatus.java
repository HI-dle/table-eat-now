package table.eat.now.coupon.user_coupon.domain.entity;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum UserCouponStatus {
  ISSUED("발급"),
  PREEMPT("선점"),
  COMMIT("사용완료"),
  ROLLBACK("사용취소"),
  ;
  private final String description;
}
