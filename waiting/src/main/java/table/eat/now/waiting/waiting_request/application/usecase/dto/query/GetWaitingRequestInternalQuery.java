package table.eat.now.waiting.waiting_request.application.usecase.dto.query;

import lombok.Builder;
import table.eat.now.common.resolver.dto.UserRole;

@Builder
public record GetWaitingRequestInternalQuery(
    Long userId,
    UserRole userRole,
    String waitingRequestUuid
) implements Query {

  public static GetWaitingRequestInternalQuery of(Long userId, UserRole userRole, String waitingRequestUuid) {

    return GetWaitingRequestInternalQuery.builder()
        .userId(userId)
        .userRole(userRole)
        .waitingRequestUuid(waitingRequestUuid)
        .build();
  }
}
