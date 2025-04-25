package table.eat.now.review.application.executor.metric;

public interface MetricRecorder {

  void countSuccess(String metricName);

  void countFailure(String metricName);

  void recordTime(String metricName, Runnable task);
}
