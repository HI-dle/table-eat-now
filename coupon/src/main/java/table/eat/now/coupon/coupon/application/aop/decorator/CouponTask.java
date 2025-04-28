package table.eat.now.coupon.coupon.application.aop.decorator;

import java.util.function.Supplier;

public interface CouponTask<T> {

  T execute(Supplier<T> supplier);
}
