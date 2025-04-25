package table.eat.now.review.application.executor.task;

import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import table.eat.now.review.application.executor.metric.MetricRecorder;

@Slf4j
@Builder
@RequiredArgsConstructor
public class CountTaskExecutor implements TaskExecutor {

  private final TaskExecutor delegate;
  private final MetricRecorder metricRecorder;
  private final String metricName;

  @Override
  public void execute(Runnable task) {
    try {
      delegate.execute(task);
      metricRecorder.countSuccess(metricName);
    } catch (RuntimeException e) {
      metricRecorder.countFailure(metricName);
      log.error("실행 중 오류 발생: {}", e.getMessage(), e);
    }
  }
}
