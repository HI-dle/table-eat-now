package table.eat.now.review.application.executor.task;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class SimpleTaskExecutorTest {

  private final SimpleTaskExecutor simpleTaskExecutor = new SimpleTaskExecutor();

  @Nested
  class execute_는 {

    @Test
    void 전달된_Runnable을_실행한다() {
      // given
      AtomicBoolean executed = new AtomicBoolean(false);
      Runnable task = () -> executed.set(true);

      // when
      simpleTaskExecutor.execute(task);

      // then
      assertTrue(executed.get());
    }
  }
}
