package table.eat.now.review.infrastructure.client.dto.response;

import java.math.BigDecimal;
import java.time.LocalTime;
import table.eat.now.review.application.client.dto.GetRestaurantInfo;

public record GetRestaurantSimpleResponse(
    String restaurantUuid,
    String name,
    String info,
    BigDecimal reviewRatingAvg,
    Integer maxReservationGuestCountPerTeamOnline,
    String contactNumber,
    String address,
    LocalTime openingAt,
    LocalTime closingAt,
    String status,
    String waitingStatus
) {
  public GetRestaurantInfo toInfo(){
    return new GetRestaurantInfo(restaurantUuid);
  }
}