package table.eat.now.review.infrastructure.redis;

import java.util.concurrent.TimeUnit;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import table.eat.now.review.application.executor.lock.LockProvider;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedissonDistributedLockProvider implements LockProvider {

  private final RedissonClient redissonClient;

  @Value("${distributed-lock.wait-time}")
  private long defaultWaitTime;
  @Value("${distributed-lock.lease-time}")
  private long defaultLeaseTime;
  @Value("${distributed-lock.time-unit}")
  private TimeUnit defaultTimeUnit;

  @Override
  public void execute(String lockKey, Runnable task) {
    tryLock(
        LockExecutionContext.builder()
            .lock(getLock(lockKey))
            .key(lockKey)
            .waitTime(defaultWaitTime)
            .leaseTime(defaultLeaseTime)
            .timeUnit(defaultTimeUnit)
            .task(task)
            .build()
    );
  }

  private RLock getLock(String lockKey) {
    return redissonClient.getLock(lockKey);
  }

  private void tryLock(LockExecutionContext ctx) {
    boolean isLocked = false;

    try {
      isLocked = ctx.lock()
          .tryLock(ctx.waitTime(), ctx.leaseTime(), ctx.timeUnit());

      if (!isLocked) {
        log.info("락 획득 실패. key={}", ctx.key());
        return;
      }
      log.info("락 획득 성공");
      ctx.task().run();

    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      log.error("락 획득 중 인터럽트 발생. key={}", ctx.key(), e);

    } catch (Exception e) {
      log.error("락 실행 중 예외 발생. key={}", ctx.key(), e);

    } finally {
      unlockSafely(ctx.lock(), ctx.key(), isLocked);
    }
  }

  private void unlockSafely(RLock lock, String lockKey, boolean isLocked) {
    if (!isLocked) {
      return;
    }
    try {
      lock.unlock();
    } catch (IllegalMonitorStateException e) {
      log.warn("이미 해제된 락. key={}", lockKey);
    } catch (Exception e) {
      log.error("락 해제 중 예외 발생. key={}", lockKey, e);
    }
  }

  @Builder
  private record LockExecutionContext(
      RLock lock,
      String key,
      long waitTime,
      long leaseTime,
      TimeUnit timeUnit,
      Runnable task
  ) {

  }
}

