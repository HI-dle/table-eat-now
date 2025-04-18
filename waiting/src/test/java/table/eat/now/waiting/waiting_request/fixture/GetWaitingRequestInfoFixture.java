package table.eat.now.waiting.waiting_request.fixture;

import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;
import table.eat.now.waiting.waiting_request.application.dto.response.GetWaitingRequestInfo;

public class GetWaitingRequestInfoFixture {

  public static GetWaitingRequestInfo create(int i, String dailyWaitingUuid, String restaurantUuid) {
    return GetWaitingRequestInfo.builder()
        .waitingRequestUuid(UUID.randomUUID().toString())
        .dailyWaitingUuid(dailyWaitingUuid)
        .restaurantUuid(restaurantUuid)
        .restaurantName("혜주네 식당")
        .phone("01000000000")
        .slackId("slackId@example.com")
        .seatSize(i % 5)
        .sequence(i)
        .rank((long) i)
        .estimatedWaitingMin(i * 20L)
        .build();
  }

  public static List<GetWaitingRequestInfo> createList(int start, int end) {
    var dailyWaitingUuid = UUID.randomUUID().toString();
    var restaurantUuid = UUID.randomUUID().toString();

    return IntStream.range(start, end)
        .mapToObj(i -> GetWaitingRequestInfoFixture.create(i, dailyWaitingUuid, restaurantUuid))
        .toList();
  }
}
