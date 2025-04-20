package table.eat.now.waiting.waiting_request.infrastructure.persistence.jpa;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import table.eat.now.waiting.waiting_request.domain.entity.WaitingRequest;

public interface JpaWaitingRequestRepository
    extends JpaRepository<WaitingRequest, Long> {

  @Query("select count(wr) > 0 from WaitingRequest wr "
      + "where wr.dailyWaitingUuid = :dailyWaitingUuid and wr.phone = :phone "
      + "and wr.status = 'WAITING' and wr.deletedAt is null")
  boolean existsByConditionAndStatusIsWaitingAndDeletedAtIsNull(
      String dailyWaitingUuid, String phone);

  Optional<WaitingRequest> findByWaitingRequestUuidAndDeletedAtIsNull(String waitingRequestUuid);

  List<WaitingRequest> findByWaitingRequestUuidInAndDeletedAtIsNull(Set<String> idsSet);
}
