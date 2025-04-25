package table.eat.now.review.application.executor;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import table.eat.now.review.application.executor.lock.LockProvider;
import table.eat.now.review.application.executor.lock.LockKey;
import table.eat.now.review.application.executor.metric.MetricName;
import table.eat.now.review.application.executor.metric.MetricRecorder;
import table.eat.now.review.application.executor.task.CountTaskExecutor;
import table.eat.now.review.application.executor.task.LockTaskExecutor;
import table.eat.now.review.application.executor.task.TimedTaskExecutor;
import table.eat.now.review.application.executor.task.SimpleTaskExecutor;
import table.eat.now.review.application.executor.task.TaskExecutor;

/**
 * 작업 실행을 위한 TaskExecutor 인스턴스를 생성하는 팩토리 클래스.
 * 데코레이터 패턴을 활용하여 메트릭 수집, 락 관리 등의 기능을 조합할 수 있습니다.
 */
@Component
@RequiredArgsConstructor
public class TaskExecutorFactory {

  private final LockProvider lockProvider;
  private final MetricRecorder metricRecorder;

  /**
   * 스케줄러 작업을 위한 TaskExecutor를 생성합니다.
   * 카운팅, 락 관리, 실행 시간 측정 기능이 포함된 실행자를 반환합니다.
   *
   * @param metricName 메트릭 수집에 사용할 메트릭 이름
   * @param lockKey 분산 락 획득에 사용할 락 키
   * @return 기능이 조합된 TaskExecutor 인스턴스
   */
  public TaskExecutor createSchedulerExecutor(MetricName metricName, LockKey lockKey) {
    TaskExecutor base = createSimpleExecutor();
    TaskExecutor counted = wrapWithCount(base, metricName);
    TaskExecutor timed = wrapWithTimer(counted, metricName);
    return wrapWithLock(timed, lockKey);
  }

  /**
   * 기본 TaskExecutor를 생성합니다.
   *
   * @return 기본 기능만 제공하는 TaskExecutor 인스턴스
   */
  private TaskExecutor createSimpleExecutor() {
    return new SimpleTaskExecutor();
  }

  /**
   * 작업 실행의 성공/실패 횟수를 카운팅하는 기능을 추가합니다.
   * 발생한 예외는 전파되지 않습니다.
   *
   * @param delegate 기존 TaskExecutor
   * @param metricName 메트릭 이름
   * @return 카운팅 기능이 추가된 TaskExecutor
   */
  private TaskExecutor wrapWithCount(TaskExecutor delegate, MetricName metricName) {
    return CountTaskExecutor.builder()
        .delegate(delegate)
        .metricRecorder(metricRecorder)
        .metricName(metricName.value())
        .build();
  }

  /**
   * 분산 락 관리 기능을 추가합니다.
   *
   * @param delegate 기존 TaskExecutor
   * @param lockKey 락 키
   * @return 락 관리 기능이 추가된 TaskExecutor
   */
  private TaskExecutor wrapWithLock(TaskExecutor delegate, LockKey lockKey) {
    return LockTaskExecutor.builder()
        .delegate(delegate)
        .lockProvider(lockProvider)
        .lockKey(lockKey.value())
        .build();
  }

  /**
   * 작업 실행 시간을 측정하는 기능을 추가합니다.
   *
   * @param delegate 기존 TaskExecutor
   * @param metricName 메트릭 이름
   * @return 시간 측정 기능이 추가된 TaskExecutor
   */
  private TaskExecutor wrapWithTimer(TaskExecutor delegate, MetricName metricName) {
    return TimedTaskExecutor.builder()
        .delegate(delegate)
        .metricRecorder(metricRecorder)
        .metricName(metricName.value())
        .build();
  }
}
