package table.eat.now.payment.payment.infrastructure.metric;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class MicrometerMetricRecorder implements MetricRecorder {

  private final MeterRegistry registry;

  @Override
  public void countSuccess(String metricName) {
    registry.counter(metricName + ".success").increment();
  }

  @Override
  public void countFailure(String metricName) {
    registry.counter(metricName + ".failure").increment();
  }

  @Override
  public void recordTime(String metricName, Runnable task) {
    Timer.Sample sample = Timer.start(registry);
    try {
      task.run();
    } finally {
      sample.stop(registry.timer(metricName + ".duration"));
    }
  }

  @Override
  public void countSuccess(String metricName, String tagKey, String tagValue) {
    registry.counter(metricName + ".success", tagKey, tagValue).increment();
  }

  @Override
  public void countFailure(String metricName, String tagKey, String tagValue) {
    registry.counter(metricName + ".failure", tagKey, tagValue).increment();
  }
}