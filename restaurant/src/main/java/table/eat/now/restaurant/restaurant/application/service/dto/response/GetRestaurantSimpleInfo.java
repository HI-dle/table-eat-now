package table.eat.now.restaurant.restaurant.application.service.dto.response;

import java.math.BigDecimal;
import java.time.LocalTime;
import lombok.Builder;
import table.eat.now.restaurant.restaurant.domain.entity.Restaurant;

@Builder
public record GetRestaurantSimpleInfo(
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
  public static GetRestaurantSimpleInfo from(Restaurant restaurant) {
    if (restaurant == null) return null;
    return GetRestaurantSimpleInfo.builder()
        .restaurantUuid(restaurant.getRestaurantUuid())
        .name(restaurant.getName())
        .info(restaurant.getInfo())
        .reviewRatingAvg(restaurant.getReviewRatingAvg())
        .maxReservationGuestCountPerTeamOnline(restaurant.getMaxReservationGuestCountPerTeamOnline())
        .contactNumber(restaurant.getContactInfo().getContactNumber())
        .address(restaurant.getContactInfo().getAddress())
        .openingAt(restaurant.getOperatingTime().getOpeningAt())
        .closingAt(restaurant.getOperatingTime().getClosingAt())
        .status(restaurant.getStatus().name())
        .waitingStatus(restaurant.getWaitingStatus().name())
        .build();
  }
}
