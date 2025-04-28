package table.eat.now.payment.payment.infrastructure.metric;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import table.eat.now.payment.payment.application.metric.RecordCount;

@Aspect
@Component
public class MetricRecorderAspect {
  private final MetricRecorder metricRecorder;

  @Autowired
  public MetricRecorderAspect(MetricRecorder metricRecorder) {
    this.metricRecorder = metricRecorder;
  }

  @Around("@annotation(recordCount)")
  public Object recordMetric(ProceedingJoinPoint joinPoint, RecordCount recordCount) throws Throwable {
    try {
      Object result = joinPoint.proceed();
      metricRecorder.countSuccess(recordCount.name().value());
      return result;
    } catch (Exception e) {
      metricRecorder.countFailure(recordCount.name().value());
      throw e;
    }
  }
}