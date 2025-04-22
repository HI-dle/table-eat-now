package table.eat.now.waiting.waiting_request.application.usecase.dto.query;

import lombok.Builder;
import table.eat.now.common.resolver.dto.UserRole;

@Builder
public record GetCurrentWaitingRequestsAdminQuery(
    int page,
    int size,
    long offset,
    Long userId,
    UserRole userRole,
    String dailyWaitingUuid
) implements Query{

  public static GetCurrentWaitingRequestsAdminQuery of(
      Long userId, UserRole userRole, String dailyWaitingUuid, int pageNumber, int pageSize, long offset) {

    return GetCurrentWaitingRequestsAdminQuery.builder()
        .page(pageNumber)
        .size(pageSize)
        .offset(offset)
        .userId(userId)
        .userRole(userRole)
        .dailyWaitingUuid(dailyWaitingUuid)
        .build();
  }
}
