package table.eat.now.restaurant.restaurant.presentation.dto.response;

import java.math.BigDecimal;
import java.time.LocalTime;
import lombok.Builder;
import table.eat.now.restaurant.restaurant.application.service.dto.response.GetRestaurantSimpleInfo;

@Builder
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
  public static GetRestaurantSimpleResponse from(GetRestaurantSimpleInfo info) {
    return GetRestaurantSimpleResponse.builder()
        .restaurantUuid(info.restaurantUuid())
        .name(info.name())
        .info(info.info())
        .reviewRatingAvg(info.reviewRatingAvg())
        .maxReservationGuestCountPerTeamOnline(info.maxReservationGuestCountPerTeamOnline())
        .contactNumber(info.contactNumber())
        .address(info.address())
        .openingAt(info.openingAt())
        .closingAt(info.closingAt())
        .status(info.status())
        .waitingStatus(info.waitingStatus())
        .build();
  }
}
