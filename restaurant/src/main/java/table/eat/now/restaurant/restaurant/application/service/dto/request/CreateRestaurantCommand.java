/**
 * @author : jieun(je-pa)
 * @Date : 2025. 04. 09.
 */
package table.eat.now.restaurant.restaurant.application.service.dto.request;

import java.time.LocalTime;
import lombok.Builder;
import table.eat.now.restaurant.restaurant.domain.entity.Restaurant;
import table.eat.now.restaurant.restaurant.domain.entity.Restaurant.RestaurantStatus;
import table.eat.now.restaurant.restaurant.domain.entity.Restaurant.WaitingStatus;

@Builder
public record CreateRestaurantCommand (
    Long ownerId,
    String name,
    String info,
    Integer maxReservationGuestCountPerTeamOnline,
    String contactNumber,
    String address,
    LocalTime openingAt,
    LocalTime closingAt
){
  public Restaurant toEntity(){
    return Restaurant.fullBuilder()
        .ownerId(ownerId)
        .name(name)
        .info(info)
        .maxReservationGuestCountPerTeamOnline(maxReservationGuestCountPerTeamOnline)
        .contactNumber(contactNumber)
        .address(address)
        .openingAt(openingAt)
        .closingAt(closingAt)
        .waitingStatus(WaitingStatus.INACTIVE)
        .status(RestaurantStatus.INACTIVE)
        .build();
  }
}
