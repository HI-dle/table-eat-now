package table.eat.now.waiting.waiting_request.domain.criteria;

import lombok.Builder;
import table.eat.now.waiting.waiting_request.application.usecase.dto.query.GetCurrentWaitingRequestsAdminQuery;

@Builder
public record CurrentWaitingRequestCriteria(
    int page,
    int size,
    String dailyWaitingUuid
) {

  public static CurrentWaitingRequestCriteria from(GetCurrentWaitingRequestsAdminQuery query) {
    return CurrentWaitingRequestCriteria.builder()
        .page(query.page())
        .size(query.size())
        .dailyWaitingUuid(query.dailyWaitingUuid())
        .build();
  }
}
