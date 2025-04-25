package table.eat.now.review.application.executor.task;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import table.eat.now.review.application.executor.metric.MetricRecorder;

class TimedTaskExecutorTest {

  private final TaskExecutor delegate = mock(TaskExecutor.class);
  private final MetricRecorder metricRecorder = mock(MetricRecorder.class);

  private final TimedTaskExecutor executor = TimedTaskExecutor.builder()
      .delegate(delegate)
      .metricRecorder(metricRecorder)
      .metricName("test_metric")
      .build();

  @Nested
  class execute_는 {

    @Test
    void recordTime_을_통해_메트릭을_기록한다() {
      // given
      Runnable dummyTask = () -> {};

      // when
      executor.execute(dummyTask);

      // then
      verify(metricRecorder, times(1)).recordTime(eq("test_metric"), any());
    }

    @Test
    void 전달된_Runnable이_실제로_실행된다() {
      // given
      AtomicBoolean executed = new AtomicBoolean(false);
      Runnable task = () -> executed.set(true);

      doAnswer(invocation -> {
        Runnable runnable = invocation.getArgument(1);
        runnable.run();
        executed.set(true);
        return null;
      }).when(metricRecorder).recordTime(eq("test_metric"), any());

      // when
      executor.execute(task);

      // then
      assertTrue(executed.get());
    }
  }
}
