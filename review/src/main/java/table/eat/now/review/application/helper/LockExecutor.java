package table.eat.now.review.application.helper;

public interface LockExecutor {

  void execute(String key, Runnable task);
}
