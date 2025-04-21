package table.eat.now.coupon.user_coupon.application.utils;

import java.util.List;
import java.util.function.Supplier;
import table.eat.now.coupon.user_coupon.application.aop.dto.LockTime;

public interface LockProvider {

  <T> T execute(List<String> keys, LockTime lockTime, Supplier<T> supplier);
}
