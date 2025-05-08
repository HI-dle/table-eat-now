package table.eat.now.coupon.user_coupon.application.aop;

import io.micrometer.core.instrument.Timer;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import table.eat.now.coupon.global.metric.MetricProvider;
import table.eat.now.coupon.user_coupon.application.aop.annotation.WithMetric;
import table.eat.now.coupon.user_coupon.application.aop.enums.UserCouponMetricInfo;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class UserCouponMetricAspect {

  private final MetricProvider metricProvider;

  @Around("@annotation(withMetric)")
  public Object metricAspect(final ProceedingJoinPoint joinPoint, WithMetric withMetric) throws Throwable {

    MethodSignature signature = (MethodSignature) joinPoint.getSignature();
    String methodName = signature.getMethod().getName();
    String className = joinPoint.getTarget().getClass().getName();
    Object[] args = joinPoint.getArgs();

    return proceedWithMetric(joinPoint, className, methodName, args, withMetric.info());
  }

  private Object proceedWithMetric(ProceedingJoinPoint joinPoint, String className, String methodName, Object[] args, UserCouponMetricInfo metricInfo) throws Throwable {

    Timer.Sample sample = metricProvider.getStartTimer();
    try {
      Object proceed = joinPoint.proceed();

      sample.stop(metricProvider.getSuccessTimer(metricInfo, className, methodName));
      metricProvider.getSuccessCounter(metricInfo, className, methodName)
          .increment();

      if (metricInfo.isBatch()) {
        Integer batchSize = getBatchSize(args);
        metricProvider.getBatchCounter(metricInfo, className, methodName).increment(batchSize);
      }

      return proceed;
    } catch (Throwable e) {

      metricProvider.getFailCounter(metricInfo, className, methodName)
          .increment();

      throw e;
    }
  }

  private Integer getBatchSize(Object[] args) {
    return Arrays.stream(args)
        .filter(arg -> arg instanceof List<?>)
        .map(arg -> ((List<?>) arg).size())
        .findAny()
        .orElse(0);
  }
}
