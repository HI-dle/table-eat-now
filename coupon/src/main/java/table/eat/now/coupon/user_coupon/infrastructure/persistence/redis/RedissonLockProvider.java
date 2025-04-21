package table.eat.now.coupon.user_coupon.infrastructure.persistence.redis;

import java.util.List;
import java.util.function.Supplier;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import table.eat.now.common.exception.CustomException;
import table.eat.now.coupon.user_coupon.application.aop.dto.LockTime;
import table.eat.now.coupon.user_coupon.application.utils.LockProvider;
import table.eat.now.coupon.user_coupon.application.utils.TransactionHelper;
import table.eat.now.coupon.user_coupon.infrastructure.exception.UserCouponInfraErrorCode;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedissonLockProvider implements LockProvider {
  private final RedissonClient redissonClient;
  private final TransactionHelper transactionHelper;

  @Override
  public <T> T execute(List<String> keys, LockTime lockTime, Supplier<T> proceedingSupplier) {

    RLock rLock = getRLock(keys);
    //validateTransaction();
    try {
      boolean available = rLock.tryLock(lockTime.waitTime(), lockTime.leaseTime(), lockTime.timeUnit());
      if (!available) {
        throw new InterruptedException();
      }
      return transactionHelper.execute(proceedingSupplier);

    } catch (InterruptedException e) {
      log.error("Redisson Lock 획득 실패 오류 {}", e.getMessage());
      throw new CustomException(UserCouponInfraErrorCode.LOCK_PROBLEM);
    } finally {
      unlock(keys, rLock);
    }
  }

  private RLock getRLock(List<String> keys) {
    if (keys.size() == 1) {
      return redissonClient.getLock(keys.get(0));
    }
    return redissonClient.getMultiLock(keys.stream().map(redissonClient::getLock).toArray(RLock[]::new));
  }

  private void unlock(List<String> keys, RLock rLock) {
    try {
      rLock.unlock();
    } catch (IllegalMonitorStateException e) {
      log.info("이미 해제된 Redisson Lock 오류 {}", keys);
    }
  }

  private void validateTransaction() {
    if(TransactionSynchronizationManager.isActualTransactionActive()) {
      // 커넥션 풀 과부하가 생기는 경우 대비
      throw CustomException.from(UserCouponInfraErrorCode.INVALID_TRANSACTION_WITH_LOCK);
    }
  }
}
