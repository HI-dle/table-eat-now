package table.eat.now.waiting.waiting_request.fixture;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;
import table.eat.now.waiting.waiting_request.domain.entity.WaitingRequest;

public class WaitingRequestFixture {

  public static WaitingRequest create(String dailyWaitingUuid, String restaurantUuid, String phone, int i) {
    return WaitingRequest.of(
        UUID.randomUUID().toString(), dailyWaitingUuid, restaurantUuid,
        (long) i, i, phone, "slack@example.com", i % 5);
  }

  public static List<WaitingRequest> createList(
      String dailyWaitingUuid, String restaurantUuid, int size) {

    return IntStream.range(0, size)
        .mapToObj(i -> WaitingRequestFixture.create(dailyWaitingUuid, restaurantUuid, getRandomPhone(), i))
        .toList();
  }

  private static String getRandomPhone() {
    int random = ThreadLocalRandom.current().nextInt(0, 100_000_000);
    return String.format("010%08d", random);
  }
}
