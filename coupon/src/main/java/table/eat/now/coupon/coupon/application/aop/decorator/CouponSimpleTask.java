package table.eat.now.coupon.coupon.application.aop.decorator;

import java.util.function.Supplier;

public class CouponSimpleTask<T> implements CouponTask<T> {

  @Override
  public T execute(Supplier<T> supplier) {
    return supplier.get();
  }
}