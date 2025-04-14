package table.eat.now.waiting.waiting_request.presentation.dto.request;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import lombok.Builder;
import table.eat.now.waiting.waiting_request.application.dto.request.EnterWaitingRequestCommand;

@Builder
public record EnterWaitingRequestRequest(
    @NotNull UUID restaurantUuid,
    @NotNull UUID dailyWaitingUuid
) {

  public EnterWaitingRequestCommand toCommand() {
    return EnterWaitingRequestCommand.builder()
        .restaurantUuid(restaurantUuid.toString())
        .dailyWaitingUuid(dailyWaitingUuid.toString())
        .build();
  }
}
