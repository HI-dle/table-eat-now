package table.eat.now.restaurant.restaurant.application.service.dto.response;

import java.math.BigDecimal;
import java.time.LocalTime;
import lombok.Builder;
import table.eat.now.restaurant.restaurant.domain.entity.Restaurant;

@Builder
public record SearchRestaurantsInfo(
    Long index,
    String restaurantUuid,
    Long ownerId,
    Long staffId,
    String name,
    BigDecimal reviewRatingAvg,
    String info,
    Integer maxReservationGuestCountPerTeamOnline,
    String waitingStatus,
    String status,
    String contactNumber,
    String address,
    LocalTime openingAt,
    LocalTime closingAt
) {

  public static SearchRestaurantsInfo from(Restaurant restaurant, Long index) {
    return SearchRestaurantsInfo.builder()
        .index(index)
        .restaurantUuid(restaurant.getRestaurantUuid())
        .ownerId(restaurant.getOwnerId())
        .staffId(restaurant.getStaffId())
        .name(restaurant.getName())
        .reviewRatingAvg(restaurant.getReviewRatingAvg())
        .info(restaurant.getInfo())
        .maxReservationGuestCountPerTeamOnline(
            restaurant.getMaxReservationGuestCountPerTeamOnline())
        .waitingStatus(restaurant.getWaitingStatus().name())
        .status(restaurant.getStatus().name())
        .address(restaurant.getContactInfo().getAddress())
        .openingAt(restaurant.getOperatingTime().getOpeningAt())
        .closingAt(restaurant.getOperatingTime().getClosingAt())
        .build();
  }
}
