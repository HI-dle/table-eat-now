package table.eat.now.waiting.waiting_request.application.usecase.dto.query;

import lombok.Builder;
import table.eat.now.common.resolver.dto.UserRole;

@Builder
public record GetWaitingRequestAdminQuery(
    Long userId,
    UserRole userRole,
    String waitingRequestUuid
) implements Query {

  public static GetWaitingRequestAdminQuery of(Long userId, UserRole userRole, String waitingRequestUuid) {

    return GetWaitingRequestAdminQuery.builder()
        .userId(userId)
        .userRole(userRole)
        .waitingRequestUuid(waitingRequestUuid)
        .build();
  }
}
