package table.eat.now.waiting.waiting.infrastructure.persistence;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import table.eat.now.common.exception.CustomException;
import table.eat.now.waiting.waiting.domain.entity.DailyWaiting;
import table.eat.now.waiting.waiting.domain.repository.WaitingRepository;
import table.eat.now.waiting.waiting.infrastructure.exception.WaitingInfraErrorCode;
import table.eat.now.waiting.waiting.infrastructure.persistence.jpa.JpaWaitingRepository;

@RequiredArgsConstructor
@Repository
public class WaitingRepositoryImpl implements WaitingRepository {
  private final JpaWaitingRepository jpaRepository;

  @Override
  public DailyWaiting getDailyWaitingBy(String dailyWaitingUuid) {
    return jpaRepository.findByDailyWaitingUuidAndDeletedAtIsNull(dailyWaitingUuid)
        .orElseThrow(() -> CustomException.from(WaitingInfraErrorCode.INVALID_DAILY_WAITING_UUID));
  }

  @Override
  public DailyWaiting save(DailyWaiting dailyWaiting) {
    return jpaRepository.save(dailyWaiting);
  }
}
