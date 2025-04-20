package table.eat.now.waiting.waiting_request.fixture;

import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;
import table.eat.now.waiting.waiting_request.application.dto.response.GetWaitingRequestInfo;

public class GetWaitingRequestInfoFixture {

  public static GetWaitingRequestInfo create(int i, String dailyWaitingUuid, String restaurantUuid, String status) {
    return GetWaitingRequestInfo.builder()
        .waitingRequestUuid(UUID.randomUUID().toString())
        .dailyWaitingUuid(dailyWaitingUuid)
        .restaurantUuid(restaurantUuid)
        .restaurantName("혜주네 식당")
        .userId(2L)
        .phone("01000000000")
        .slackId("slackId@example.com")
        .seatSize(i % 4 + 1)
        .sequence(i + 1)
        .status(status)
        .rank(!"WAITING".equals(status) ? null : (long) i)
        .estimatedWaitingMin(!"WAITING".equals(status) ? null : (i + 1) * 20L)
        .build();
  }

  public static List<GetWaitingRequestInfo> createList(int start, int end) {
    var dailyWaitingUuid = UUID.randomUUID().toString();
    var restaurantUuid = UUID.randomUUID().toString();
    var statusList = List.of("WAITING", "SEATED", "LEAVED");

    return IntStream.range(start, end)
      .mapToObj(i -> GetWaitingRequestInfoFixture.create(i, dailyWaitingUuid, restaurantUuid, statusList.get(i % 3)))
        .toList();
  }
}
