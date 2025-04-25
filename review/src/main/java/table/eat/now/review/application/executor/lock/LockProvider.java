package table.eat.now.review.application.executor.lock;

public interface LockProvider {

  void execute(String key, Runnable task);
}
