package table.eat.now.review.application.executor.task;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import table.eat.now.review.application.executor.lock.LockProvider;

import static org.mockito.Mockito.*;

class LockTaskExecutorTest {

  private TaskExecutor delegate;
  private LockProvider lockProvider;
  private LockTaskExecutor lockTaskExecutor;
  private final String lockKey = "test-lock";

  @Nested
  class execute_는 {

    @BeforeEach
    void setUp() {
      delegate = mock(TaskExecutor.class);
      lockProvider = mock(LockProvider.class);

      lockTaskExecutor = LockTaskExecutor.builder()
          .delegate(delegate)
          .lockProvider(lockProvider)
          .lockKey(lockKey)
          .build();
    }

    @Test
    void 락_획득_후에_delegate_를_실행_할_수_있다() {
      // given
      Runnable task = mock(Runnable.class);

      // when
      lockTaskExecutor.execute(task);

      // then
      verify(lockProvider).execute(eq(lockKey), any(Runnable.class));
    }

    @Test
    void 락_내부에서_delegate_가_실행_될_수_있다() {
      // given
      Runnable task = mock(Runnable.class);

      doAnswer(invocation -> {
        Runnable inner = invocation.getArgument(1);
        inner.run();
        return null;
      }).when(lockProvider).execute(eq(lockKey), any(Runnable.class));

      // when
      lockTaskExecutor.execute(task);

      // then
      verify(delegate).execute(task);
    }
  }
}
