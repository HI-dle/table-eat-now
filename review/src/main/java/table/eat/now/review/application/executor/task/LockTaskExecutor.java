package table.eat.now.review.application.executor.task;

import lombok.Builder;
import lombok.RequiredArgsConstructor;
import table.eat.now.review.application.executor.lock.LockProvider;

@Builder
@RequiredArgsConstructor
public class LockTaskExecutor implements TaskExecutor {

  private final TaskExecutor delegate;
  private final LockProvider lockProvider;
  private final String lockKey;

  @Override
  public void execute(Runnable task) {
    lockProvider.execute(
        lockKey, () -> delegate.execute(task)
    );
  }
}