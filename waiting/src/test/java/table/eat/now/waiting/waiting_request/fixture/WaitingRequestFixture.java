package table.eat.now.waiting.waiting_request.fixture;

import java.util.UUID;
import table.eat.now.waiting.waiting_request.domain.entity.WaitingRequest;

public class WaitingRequestFixture {

  public static WaitingRequest create(String dailyWaitingUuid, String restaurantUuid, String phone, int i) {
    return WaitingRequest.of(
        UUID.randomUUID().toString(), dailyWaitingUuid, restaurantUuid,
        2L, i, phone, "slack@example.com", 3);
  }
}
