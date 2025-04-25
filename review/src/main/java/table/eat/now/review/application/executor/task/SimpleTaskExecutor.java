package table.eat.now.review.application.executor.task;

public class SimpleTaskExecutor implements TaskExecutor {

  @Override
  public void execute(Runnable task) {
    task.run();
  }
}