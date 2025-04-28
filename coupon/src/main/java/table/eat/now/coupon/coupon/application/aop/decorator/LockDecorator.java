package table.eat.now.coupon.coupon.application.aop.decorator;

import java.util.List;
import java.util.function.Supplier;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import table.eat.now.coupon.coupon.application.aop.dto.LockTime;
import table.eat.now.coupon.coupon.application.utils.LockProvider;

@Builder
@RequiredArgsConstructor
public class LockDecorator<T> implements Task<T> {

  private final Task<T> delegate;
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