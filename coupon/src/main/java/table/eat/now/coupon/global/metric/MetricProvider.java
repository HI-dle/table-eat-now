package table.eat.now.coupon.global.metric;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.Timer.Sample;
import java.util.concurrent.atomic.AtomicLong;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.common.TopicPartition;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class MetricProvider {

  private static final String TIMER_SUFFIX = ".timer";
  private static final String BATCH_SUFFIX = ".batch";
  private static final String KAFKA_CONSUMER_LAG = "kafka.consumer.lag";
  public static final String FAIL = "fail";
  public static final String SUCCESS = "success";
  public static final String STATUS_TAG = "status";
  public static final String CLASS_TAG = "class";
  public static final String METHOD_TAG = "method";


  private final MeterRegistry meterRegistry;

  public Sample getStartTimer() {
    return Timer.start(meterRegistry);
  }

  public Timer getSuccessTimer(MetricInfo metricInfo,  String className, String methodName) {
    return this.getTimer(metricInfo, className, methodName, SUCCESS);
  }

  public Counter getSuccessCounter(MetricInfo metricInfo, String className, String methodName) {
    return this.getCounter(metricInfo, className, methodName, SUCCESS);
  }

  public Counter getFailCounter(MetricInfo metricInfo, String className, String methodName) {
    return this.getCounter(metricInfo, className, methodName, FAIL);
  }

  private Timer getTimer(MetricInfo metricInfo, String className, String methodName, String status) {

    return Timer.builder(metricInfo.getName() + TIMER_SUFFIX)
        .tag(CLASS_TAG, className)
        .tag(METHOD_TAG, methodName)
        .tag(STATUS_TAG, status)
        .register(meterRegistry);
  }

  private Counter getCounter(MetricInfo metricInfo, String className, String methodName, String status) {

    return Counter.builder(metricInfo.getName())
        .tag(CLASS_TAG, className)
        .tag(METHOD_TAG, methodName)
        .tag(STATUS_TAG, status)
        .register(meterRegistry);
  }

  public Counter getBatchCounter(MetricInfo metricInfo, String className, String methodName) {

    return Counter.builder(metricInfo.getName() + BATCH_SUFFIX)
        .tag(CLASS_TAG, className)
        .tag(METHOD_TAG, methodName)
        .tag(STATUS_TAG, SUCCESS)
        .register(meterRegistry);
  }

  public Gauge getLagGauge(String groupId, TopicPartition partition, AtomicLong value) {

    return Gauge.builder(KAFKA_CONSUMER_LAG, value, AtomicLong::get)
        .tag("group", groupId)
        .tag("topic",  partition.topic())
        .tag("partition", String.valueOf(partition.partition()))
        .register(meterRegistry);
  }
}
