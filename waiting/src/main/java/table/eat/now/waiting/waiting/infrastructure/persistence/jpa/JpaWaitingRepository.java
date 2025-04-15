package table.eat.now.waiting.waiting.infrastructure.persistence.jpa;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import table.eat.now.waiting.waiting.domain.entity.DailyWaiting;

public interface JpaWaitingRepository extends JpaRepository<DailyWaiting, Long> {

  Optional<DailyWaiting> findByDailyWaitingUuidAndDeletedAtIsNull(String dailyWaitingUuid);
}
