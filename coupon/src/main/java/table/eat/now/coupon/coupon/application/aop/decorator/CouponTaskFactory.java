package table.eat.now.coupon.coupon.application.aop.decorator;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import table.eat.now.coupon.coupon.application.aop.dto.LockTime;
import table.eat.now.coupon.coupon.application.utils.LockProvider;

/**
 * 작업 실행을 위한 Task 인스턴스를 생성하는 팩토리 클래스.
 * 데코레이터 패턴을 활용하여 메트릭 수집, 락 관리 등의 기능을 조합할 수 있습니다.
 */
@Component
@RequiredArgsConstructor
public class CouponTaskFactory<T> {

  private final LockProvider lockProvider;

  /**
   * 기본 Task를 생성합니다.
   *
   * @return 기본 기능만 제공하는 Task 인스턴스
   */
  private Task<T> createSimpleTask() {
    return new SimpleTask<>();
  }

  /**
   * 보완된 작업을 위한 DecoratedTask를 생성합니다.
   * 락 관리, 트랜잭션 적용, 카운팅, 실행 시간 측정 기능이 포함된 실행자를 반환합니다.
   *
   * @param condiment 기능 첨가
   * @return 기능이 조합된 Task 인스턴스
   */
  public Task<T> createDecoratedTask(TaskCondiment condiment) {

    Task<T> base = createSimpleTask();
    //Task counted = wrapWithCount(base, metricName);
    //Task timed = wrapWithTimer(counted, metricName);
    Task<T> transactioned = decoWithTransaction(base, condiment.transactional(), condiment.readOnly());
    Task<T> locked = decoWithDistributedLock(transactioned, condiment.lockKeys(), condiment.lockTime());
    return locked;
  }

  /**
   * 트랜잭션 관리 일부 기능을 선택적으로 추가합니다.
   *
   * @param delegate 기존 Task
   * @param transactional 트랜잭션 처리 여부
   * @param readOnly 트랜잭션 읽기 옵션 설정
   * @return 트랜잭션 관리 기능이 추가된 Task
   */
  private Task<T> decoWithTransaction(Task<T> delegate, boolean transactional, boolean readOnly) {

    if (!transactional) return delegate;
    
    if (!readOnly) {
      return TransactionDecorator.<T>builder()
          .delegate(delegate)
          .build();
    }
    return TransactionReadOnlyDecorator.<T>builder()
        .delegate(delegate)
        .build();
  }

  /**
   * 분산 락 관리 기능을 추가합니다.
   *
   * @param delegate 기존 Task
   * @param lockKeys 락 키 리스트
   * @param lockTime 락 시간 정보
   * @return 락 관리 기능이 추가된 Task
   */
  private Task<T> decoWithDistributedLock(Task<T> delegate, List<String> lockKeys, LockTime lockTime) {
    if (lockKeys == null || lockKeys.isEmpty()) {
      return delegate;
    }

    return LockDecorator.<T>builder()
        .delegate(delegate)
        .lockProvider(lockProvider)
        .lockKeys(lockKeys)
        .lockTime(lockTime)
        .build();
  }
}
