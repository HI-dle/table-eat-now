package table.eat.now.waiting.waiting_request.domain.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class WaitingRequestDomainService {

  public Long calculateEstimatedWaitingMin(Long avgWaitingSec, Long rank) {
    if (rank == null || avgWaitingSec == null) return null;
    return avgWaitingSec * (rank + 1L) / 60L;
  }
}
