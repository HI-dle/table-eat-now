package table.eat.now.waiting.waiting_request.application.usecase.dto.command;

import lombok.Builder;
import table.eat.now.common.resolver.dto.UserRole;
import table.eat.now.waiting.waiting_request.domain.entity.WaitingRequest;
import table.eat.now.waiting.waiting_request.domain.entity.WaitingRequestHistory;
import table.eat.now.waiting.waiting_request.domain.entity.WaitingStatus;

@Builder
public record CreateWaitingRequestCommand(
    Long userId,
    UserRole userRole,
    String dailyWaitingUuid,
    String phone,
    String slackId,
    int seatSize
) implements Command {

  public WaitingRequest toEntity(String waitingRequestUuid, String restaurantUuid, Long userId, Long sequence) {
    if (sequence < Integer.MIN_VALUE || sequence > Integer.MAX_VALUE) {
      throw new IllegalArgumentException("시퀀스 값이 범위를 초과합니다.");
    }

    WaitingRequest waitingRequest = WaitingRequest.of(
        waitingRequestUuid, dailyWaitingUuid, restaurantUuid,
        userId, sequence.intValue(), phone, slackId, seatSize);
    WaitingRequestHistory history = WaitingRequestHistory.of(WaitingStatus.WAITING);
    waitingRequest.addHistory(history);
    return waitingRequest;
  }
}
