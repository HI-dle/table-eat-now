package table.eat.now.coupon.user_coupon.application.aop.decorator;

import java.util.function.Supplier;

public interface UserCouponTask<T> {

  T execute(Supplier<T> supplier);
}
