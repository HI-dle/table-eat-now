package table.eat.now.waiting.waiting_request.application.usecase.dto.query;

import lombok.Builder;
import table.eat.now.common.resolver.dto.UserRole;

@Builder
public record GetWaitingRequestQuery(
    Long userId,
    UserRole userRole,
    String waitingRequestUuid,
    String phone
) implements Query {

  public static GetWaitingRequestQuery of(
      Long userId, UserRole userRole, String waitingRequestUuid, String phone) {

    return GetWaitingRequestQuery.builder()
        .userId(userId)
        .userRole(userRole)
        .waitingRequestUuid(waitingRequestUuid)
        .phone(phone)
        .build();
  }
}
