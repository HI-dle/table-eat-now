/**
 * @author : jieun(je-pa)
 * @Date : 2025. 04. 10.
 */
package table.eat.now.restaurant.global.fixture;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import table.eat.now.restaurant.restaurant.domain.entity.Restaurant;
import table.eat.now.restaurant.restaurant.domain.entity.Restaurant.RestaurantStatus;
import table.eat.now.restaurant.restaurant.domain.entity.Restaurant.WaitingStatus;
import table.eat.now.restaurant.restaurant.domain.entity.RestaurantMenu;
import table.eat.now.restaurant.restaurant.domain.entity.RestaurantTimeSlot;
import table.eat.now.restaurant.restaurant.domain.entity.vo.ContactInfo;
import table.eat.now.restaurant.restaurant.domain.entity.vo.OperatingTime;

public class RestaurantFixture {

  public static Restaurant createRestaurant(
      Long id,
      String restaurantUuid,
      Long ownerId,
      Long staffId,
      String name,
      BigDecimal reviewRatingAvg,
      String info,
      Integer maxReservationGuestCountPerTeamOnline,
      String contactNumber,
      String address,
      LocalTime openingAt,
      LocalTime closingAt,
      WaitingStatus waitingStatus,
      RestaurantStatus status,
      List<RestaurantMenu> menus,
      List<RestaurantTimeSlot> timeSlots
  ) {

    return Restaurant.fullBuilder()
        .id(id)
        .restaurantUuid(restaurantUuid)
        .ownerId(ownerId)
        .staffId(staffId)
        .name(name)
        .reviewRatingAvg(reviewRatingAvg)
        .info(info)
        .maxReservationGuestCountPerTeamOnline(maxReservationGuestCountPerTeamOnline)
        .contactInfo(ContactInfo.of(contactNumber, address))
        .operatingTime(OperatingTime.of(openingAt, closingAt))
        .waitingStatus(waitingStatus)
        .status(status)
        .menus(menus != null ? menus : new ArrayList<>())
        .timeSlots(timeSlots != null ? timeSlots : new ArrayList<>())
        .build();
  }
}
