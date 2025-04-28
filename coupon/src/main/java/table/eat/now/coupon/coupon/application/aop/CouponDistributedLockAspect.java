package table.eat.now.coupon.coupon.application.aop;

import java.lang.reflect.Method;
import java.util.List;
import java.util.function.Supplier;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import table.eat.now.coupon.coupon.application.aop.annotation.DistributedLock;
import table.eat.now.coupon.coupon.application.aop.annotation.WithSimpleTransaction;
import table.eat.now.coupon.coupon.application.aop.decorator.CouponTaskFactory;
import table.eat.now.coupon.coupon.application.aop.decorator.CouponTask;
import table.eat.now.coupon.coupon.application.aop.decorator.CouponTaskCondiment;
import table.eat.now.coupon.coupon.application.aop.dto.LockTime;
import table.eat.now.coupon.coupon.application.utils.DistributedLockKeyGenerator;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class CouponDistributedLockAspect {

  private final CouponTaskFactory<Object> taskFactory;

  @Around("@annotation(distributedLock)")
  public Object lock(final ProceedingJoinPoint joinPoint, DistributedLock distributedLock) throws Throwable {

    MethodSignature signature = (MethodSignature) joinPoint.getSignature();
    Method method = signature.getMethod();
    WithSimpleTransaction withSimpleTransaction = method.getAnnotation(WithSimpleTransaction.class);

    List<String> keys = DistributedLockKeyGenerator.generateKeys(
        distributedLock.subPrefix(),
        distributedLock.key(),
        signature.getParameterNames(),
        joinPoint.getArgs());
    LockTime lockTime = LockTime.from(distributedLock);

    CouponTask<Object> decoratedCouponTask = taskFactory.createDecoratedTask(
        CouponTaskCondiment.of(keys, lockTime, withSimpleTransaction));

    return decoratedCouponTask.execute(proceedSupplier(joinPoint));
  }

  private Supplier<Object> proceedSupplier(ProceedingJoinPoint joinPoint) {
    return () -> {
      try {
        return joinPoint.proceed();
      } catch (Throwable e) {
        throw e instanceof RuntimeException ? (RuntimeException) e : new RuntimeException(e);
      }
    };
  }
}
