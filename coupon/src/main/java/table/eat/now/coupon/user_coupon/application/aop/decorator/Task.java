package table.eat.now.coupon.user_coupon.application.aop.decorator;

import java.util.function.Supplier;

public interface Task<T> {

  T execute(Supplier<T> supplier);
}
