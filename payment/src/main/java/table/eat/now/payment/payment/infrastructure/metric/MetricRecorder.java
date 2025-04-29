package table.eat.now.payment.payment.infrastructure.metric;

public interface MetricRecorder {

  void countSuccess(String metricName);

  void countFailure(String metricName);

  void recordTime(String metricName, Runnable task);

  void countSuccess(String metricName, String tagKey, String tagValue);

  void countFailure(String metricName, String tagKey, String tagValue);
}
