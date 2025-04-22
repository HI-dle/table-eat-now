package table.eat.now.waiting.waiting_request.application.usecase.dto.command;

import lombok.Builder;
import table.eat.now.common.resolver.dto.UserRole;

@Builder
public record WaitingRequestEntranceAdminCommand(
    Long userId,
    UserRole userRole,
    String waitingRequestUuid
) implements Command {

  public static WaitingRequestEntranceAdminCommand of(Long userId, UserRole userRole, String waitingRequestUuid) {

    return WaitingRequestEntranceAdminCommand.builder()
        .userId(userId)
        .userRole(userRole)
        .waitingRequestUuid(waitingRequestUuid)
        .build();
  }
}
