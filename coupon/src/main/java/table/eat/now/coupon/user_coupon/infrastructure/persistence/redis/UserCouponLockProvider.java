package table.eat.now.coupon.user_coupon.infrastructure.persistence.redis;

import java.util.List;
import java.util.function.Supplier;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;
import table.eat.now.common.exception.CustomException;
import table.eat.now.coupon.user_coupon.application.aop.dto.LockTime;
import table.eat.now.coupon.user_coupon.application.utils.LockProvider;
import table.eat.now.coupon.user_coupon.application.utils.TransactionHelper;
import table.eat.now.coupon.user_coupon.infrastructure.exception.UserCouponInfraErrorCode;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserCouponLockProvider implements LockProvider {
  private final RedissonClient userCouponRedissonClient;
  private final TransactionHelper transactionHelper;

  @Override
  public <T> T execute(List<String> keys, LockTime lockTime, Supplier<T> proceedingSupplier) {

    RLock rLock = getRLock(keys);
    boolean available = false;
    //transactionHelper.validateTransaction();
    try {
      available = rLock.tryLock(lockTime.waitTime(), lockTime.leaseTime(), lockTime.timeUnit());
      if (!available) {
        throw new InterruptedException();
      }
      return transactionHelper.execute(proceedingSupplier);

    } catch (InterruptedException e) {
      log.error("Redisson Lock 획득 실패 오류 {}", e.getMessage());
      throw new CustomException(UserCouponInfraErrorCode.LOCK_PROBLEM);
    } finally {
      if (available) {
        unlock(keys, rLock);
      }
    }
  }

  private RLock getRLock(List<String> keys) {
    if (keys.size() == 1) {
      return userCouponRedissonClient.getLock(keys.get(0));
    }
    return userCouponRedissonClient.getMultiLock(keys.stream().map(userCouponRedissonClient::getLock).toArray(RLock[]::new));
  }

  private void unlock(List<String> keys, RLock rLock) {
    try {
      rLock.unlock();
    } catch (IllegalMonitorStateException e) {
      log.info("이미 해제된 Redisson Lock 오류 {}", keys);
    }
  }
}
