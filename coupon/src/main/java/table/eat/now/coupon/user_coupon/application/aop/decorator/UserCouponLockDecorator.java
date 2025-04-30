package table.eat.now.coupon.user_coupon.application.aop.decorator;

import java.util.List;
import java.util.function.Supplier;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import table.eat.now.coupon.user_coupon.application.aop.dto.LockTime;
import table.eat.now.coupon.user_coupon.application.utils.LockProvider;

@Builder
@RequiredArgsConstructor
public class UserCouponLockDecorator<T> implements
    UserCouponTask<T> {

  private final UserCouponTask<T> delegate;
  private final LockProvider lockProvider;
  private final List<String> lockKeys;
  private final LockTime lockTime;

  @Override
  public T execute(Supplier<T> task) {
    return lockProvider.execute(
        lockKeys,
        lockTime,
        () -> delegate.execute(task)
    );
  }
}