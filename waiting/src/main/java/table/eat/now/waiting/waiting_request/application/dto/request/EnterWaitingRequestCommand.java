package table.eat.now.waiting.waiting_request.application.dto.request;

import lombok.Builder;

@Builder
public record EnterWaitingRequestCommand(
    String restaurantUuid,
    String dailyWaitingUuid
) {

}
