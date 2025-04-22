package table.eat.now.waiting.waiting_request.application.usecase.dto.command;

import lombok.Builder;
import table.eat.now.common.resolver.dto.UserRole;

@Builder
public record UpdateWaitingRequestStatusAdminCommand(
    Long userId,
    UserRole userRole,
    String waitingRequestUuid,
    String type
) implements Command {

  public static UpdateWaitingRequestStatusAdminCommand of(
      Long userId, UserRole userRole, String waitingRequestUuid, String type) {

    return UpdateWaitingRequestStatusAdminCommand.builder()
        .userId(userId)
        .userRole(userRole)
        .waitingRequestUuid(waitingRequestUuid)
        .type(type)
        .build();
  }
}
