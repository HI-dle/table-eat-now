package table.eat.now.review.application.executor.task;

import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import table.eat.now.review.application.executor.metric.MetricRecorder;

class CountTaskExecutorTest {

  private TaskExecutor delegate;
  private MetricRecorder metricRecorder;
  private CountTaskExecutor countTaskExecutor;
  private final String metricName = "test_metric";

  @Nested
  class execute_는 {

    @BeforeEach
    void setUp() {
      delegate = mock(TaskExecutor.class);
      metricRecorder = mock(MetricRecorder.class);

      countTaskExecutor = CountTaskExecutor.builder()
          .delegate(delegate)
          .metricRecorder(metricRecorder)
          .metricName(metricName)
          .build();
    }

    @Test
    void 정상_실행_시_countSuccess_를_호출할_수_있다() {
      // given
      Runnable task = mock(Runnable.class);

      // when
      countTaskExecutor.execute(task);

      // then
      verify(delegate).execute(task);
      verify(metricRecorder).countSuccess(metricName);
      verify(metricRecorder, never()).countFailure(anyString());
    }

    @Test
    void 예외_발생_시_countFailure_를_호출할_수_있다() {
      // given
      Runnable task = mock(Runnable.class);
      doThrow(new RuntimeException("예외")).when(delegate).execute(task);

      // when
      countTaskExecutor.execute(task);

      // then
      verify(delegate).execute(task);
      verify(metricRecorder, never()).countSuccess(anyString());
      verify(metricRecorder).countFailure(metricName);
    }

  }
}
