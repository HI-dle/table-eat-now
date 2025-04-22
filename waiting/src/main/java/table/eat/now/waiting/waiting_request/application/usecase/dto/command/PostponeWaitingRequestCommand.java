package table.eat.now.waiting.waiting_request.application.usecase.dto.command;

import lombok.Builder;
import table.eat.now.common.resolver.dto.UserRole;

@Builder
public record PostponeWaitingRequestCommand(
    Long userId,
    UserRole userRole,
    String waitingRequestUuid,
    String phone
) implements Command {

  public static PostponeWaitingRequestCommand of(
      Long userId, UserRole userRole, String waitingRequestUuid, String phone) {

    return PostponeWaitingRequestCommand.builder()
        .userId(userId)
        .userRole(userRole)
        .waitingRequestUuid(waitingRequestUuid)
        .phone(phone)
        .build();
  }
}
