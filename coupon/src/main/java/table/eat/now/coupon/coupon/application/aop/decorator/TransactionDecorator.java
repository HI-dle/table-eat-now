package table.eat.now.coupon.coupon.application.aop.decorator;

import java.util.function.Supplier;
import lombok.Builder;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Builder
public class TransactionDecorator<T> implements Task<T> {
  private final Task<T> delegate;

  @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
  @Override
  public T execute(Supplier<T> supplier) {
    return supplier.get();
  }
}
