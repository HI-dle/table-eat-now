package table.eat.now.coupon.user_coupon.application.aop.decorator;

import java.util.function.Supplier;

public class UserCouponSimpleTask<T> implements UserCouponTask<T> {

  @Override
  public T execute(Supplier<T> supplier) {
    return supplier.get();
  }
}