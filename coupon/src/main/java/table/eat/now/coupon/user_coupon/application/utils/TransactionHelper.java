package table.eat.now.coupon.user_coupon.application.utils;

import java.util.function.Supplier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import table.eat.now.common.exception.CustomException;
import table.eat.now.coupon.user_coupon.application.exception.UserCouponErrorCode;

@Component
public class TransactionHelper {

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public <T> T execute(final Supplier<T> supplier) {
    return supplier.get();
  }

  private void validateTransaction() {
    if(TransactionSynchronizationManager.isActualTransactionActive()) {
      // 커넥션 풀 과부하가 생기는 경우 대비
      throw CustomException.from(UserCouponErrorCode.INVALID_TRANSACTION_WITH_LOCK);
    }
  }
}
