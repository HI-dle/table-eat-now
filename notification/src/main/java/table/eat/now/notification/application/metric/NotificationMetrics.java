package table.eat.now.notification.application.metric;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 21.
 */
@Component
@RequiredArgsConstructor
public class NotificationMetrics {

  private final MeterRegistry registry;

  public Timer.Sample startSendTimer() {
    return Timer.start(registry);
  }

  public void recordSendLatency(Timer.Sample sample, String method) {
    sample.stop(Timer.builder("notification.send.latency")
        .description("알림 전송 지연 시간")
        .tag("method", method)
        .register(registry));
  }

  public void incrementSendSuccess() {
    registry.counter("notification.send.success.count").increment();
  }

  public void incrementSendFail() {
    registry.counter("notification.send.fail.count").increment();
  }

  public void incrementCreate() {
    registry.counter("notification.create.count").increment();
  }

  public void recordFetchedScheduledCount(int count) {
    registry.counter("notification.scheduled.fetch.count").increment(count);
  }

  public void recordSchedulerExecution(Runnable runnable) {
    Timer timer = Timer.builder("notification.scheduler.execution.duration")
        .description("스케줄러 전체 실행 시간")
        .register(registry);
    timer.record(runnable);
  }

}
