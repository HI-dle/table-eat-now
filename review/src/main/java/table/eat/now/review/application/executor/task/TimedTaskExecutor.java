package table.eat.now.review.application.executor.task;

import lombok.Builder;
import lombok.RequiredArgsConstructor;
import table.eat.now.review.application.executor.metric.MetricRecorder;

@Builder
@RequiredArgsConstructor
public class TimedTaskExecutor implements TaskExecutor {

  private final TaskExecutor delegate;
  private final MetricRecorder metricRecorder;
  private final String metricName;

  @Override
  public void execute(Runnable task) {
    metricRecorder.recordTime(
        metricName, () -> delegate.execute(task)
    );
  }
}