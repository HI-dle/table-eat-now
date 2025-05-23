package table.eat.now.review.application.executor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import java.lang.reflect.Field;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import table.eat.now.review.application.executor.lock.LockKey;
import table.eat.now.review.application.executor.lock.LockProvider;
import table.eat.now.review.application.executor.metric.MetricName;
import table.eat.now.review.application.executor.metric.MetricRecorder;
import table.eat.now.review.application.executor.task.LockTaskExecutor;
import table.eat.now.review.application.executor.task.TaskExecutor;
import table.eat.now.review.application.executor.task.TimedTaskExecutor;

class TaskExecutorFactoryTest {

  private final LockProvider lockProvider = mock(LockProvider.class);
  private final MetricRecorder metricRecorder = mock(MetricRecorder.class);
  private final TaskExecutorFactory factory = new TaskExecutorFactory(lockProvider, metricRecorder);


  @Nested
  class createSchedulerExecutor_는 {

    @Test
    void 스케줄러를_위한_데코레이터_체인을_생성할_수_있다() throws Exception {
      // when
      TaskExecutor executor = factory.createSchedulerExecutor(
          MetricName.RATING_UPDATE_RECENT, LockKey.RATING_UPDATE_RECENT);

      // then
      assertThat(executor).isInstanceOf(LockTaskExecutor.class);
      Field delegateField = LockTaskExecutor.class.getDeclaredField("delegate");
      delegateField.setAccessible(true);
      TaskExecutor innerExecutor = (TaskExecutor) delegateField.get(executor);
      assertThat(innerExecutor).isInstanceOf(TimedTaskExecutor.class);
    }
  }
}
