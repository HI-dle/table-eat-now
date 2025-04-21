package table.eat.now.coupon.user_coupon.application.aop;

import java.util.List;
import java.util.function.Supplier;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import table.eat.now.coupon.user_coupon.application.aop.annotation.DistributedLock;
import table.eat.now.coupon.user_coupon.application.aop.dto.LockTime;
import table.eat.now.coupon.user_coupon.application.utils.DistributedLockKeyGenerator;
import table.eat.now.coupon.user_coupon.application.utils.LockProvider;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class DistributedLockAspect {

  private final LockProvider lockProvider;

  @Around("@annotation(distributedLock)")
  public Object lock(final ProceedingJoinPoint joinPoint, DistributedLock distributedLock) throws Throwable {

    MethodSignature signature = (MethodSignature) joinPoint.getSignature();

    List<String> keys = DistributedLockKeyGenerator.generateKeys(
        distributedLock.key(), signature.getParameterNames(), joinPoint.getArgs());
    LockTime lockTime = LockTime.from(distributedLock);

    return lockProvider.execute(keys, lockTime, proceedAsSupplier(joinPoint));
  }

  private Supplier<Object> proceedAsSupplier(ProceedingJoinPoint joinPoint) {
    return () -> {
      try {
        return joinPoint.proceed();
      } catch (Throwable e) {
        throw new RuntimeException(e);
      }
    };
  }
}
