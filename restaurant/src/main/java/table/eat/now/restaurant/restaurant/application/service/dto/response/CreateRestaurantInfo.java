/**
 * @author : jieun(je-pa)
 * @Date : 2025. 04. 09.
 */
package table.eat.now.restaurant.restaurant.application.service.dto.response;

import java.time.LocalTime;
import lombok.Builder;
import table.eat.now.restaurant.restaurant.domain.entity.Restaurant;

@Builder
public record CreateRestaurantInfo (
    String restaurantUuid,
    Long ownerId,
    String name,
    String info,
    Integer maxReservationGuestCountPerTeamOnline,
    String contactNumber,
    String address,
    LocalTime openingAt,
    LocalTime closingAt,
    String status,
    String waitingStatus
){

  public static CreateRestaurantInfo from(Restaurant restaurant) {
    return CreateRestaurantInfo.builder()
        .restaurantUuid(restaurant.getRestaurantUuid())
        .ownerId(restaurant.getOwnerId())
        .name(restaurant.getName())
        .info(restaurant.getInfo())
        .maxReservationGuestCountPerTeamOnline(restaurant.getMaxReservationGuestCountPerTeamOnline())
        .contactNumber(restaurant.getContactInfo().getContactNumber())
        .address(restaurant.getContactInfo().getAddress())
        .openingAt(restaurant.getOperatingTime().getOpeningAt())
        .closingAt(restaurant.getOperatingTime().getClosingAt())
        .status(restaurant.getStatus().getName())
        .waitingStatus(restaurant.getWaitingStatus().getName())
        .build();
  }
}
