package table.eat.now.coupon.coupon.application.aop.decorator;

import java.util.function.Supplier;

public class SimpleTask<T> implements Task<T> {

  @Override
  public T execute(Supplier<T> supplier) {
    return supplier.get();
  }
}