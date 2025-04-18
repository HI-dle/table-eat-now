package table.eat.now.waiting.waiting_request.domain.criteria;

import lombok.Builder;
import org.springframework.data.domain.Pageable;

@Builder
public record CurrentWaitingRequestCriteria(
    int page,
    int size,
    String dailyWaitingUuid
) {

  public static CurrentWaitingRequestCriteria from(Pageable pageable, String dailyWaitingUuid) {
    return CurrentWaitingRequestCriteria.builder()
        .page(pageable.getPageNumber())
        .size(pageable.getPageSize())
        .dailyWaitingUuid(dailyWaitingUuid)
        .build();
  }
}
