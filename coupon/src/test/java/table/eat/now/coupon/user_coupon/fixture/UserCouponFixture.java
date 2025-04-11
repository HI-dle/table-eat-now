package table.eat.now.coupon.user_coupon.fixture;

import java.time.LocalDateTime;
import java.util.UUID;
import table.eat.now.coupon.user_coupon.domain.entity.UserCoupon;

public class UserCouponFixture {

  public static UserCoupon create(long userId) {
    return UserCoupon.of(UUID.randomUUID().toString(), UUID.randomUUID().toString(), userId,
        "test 사용자 쿠폰", LocalDateTime.of(2025, 11, 30, 0,0));
  }
}
