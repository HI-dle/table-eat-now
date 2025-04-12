package table.eat.now.waiting.waiting_request.application.dto.request;

import lombok.Builder;
import table.eat.now.waiting.waiting_request.domain.entity.WaitingRequest;

@Builder
public record CreateWaitingRequestCommand(
    String dailyWaitingUuid,
    String phone,
    String slackId,
    int seatSize
) {

  public WaitingRequest toEntity(String waitingRequestUuid, Long userId, Long sequence) {
    if (sequence < Integer.MIN_VALUE || sequence > Integer.MAX_VALUE) {
      throw new IllegalArgumentException("시퀀스 값이 범위를 초과합니다.");
    }
    return WaitingRequest.of(
        waitingRequestUuid, dailyWaitingUuid, userId, sequence.intValue(), phone, slackId, seatSize);
  }
}
