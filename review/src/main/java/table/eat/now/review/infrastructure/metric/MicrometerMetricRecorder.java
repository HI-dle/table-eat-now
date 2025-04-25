package table.eat.now.review.infrastructure.metric;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import table.eat.now.review.application.executor.metric.MetricRecorder;

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
}