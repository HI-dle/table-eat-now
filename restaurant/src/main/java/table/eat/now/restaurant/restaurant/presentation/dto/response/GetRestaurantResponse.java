/**
 * @author : jieun(je-pa)
 * @Date : 2025. 04. 15.
 */
package table.eat.now.restaurant.restaurant.presentation.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import lombok.Builder;
import table.eat.now.restaurant.restaurant.application.service.dto.response.GetRestaurantInfo;

@Builder
public record GetRestaurantResponse (
    String restaurantUuid,
    String name,
    Long ownerId,
    Long staffId,
    String info,
    BigDecimal reviewRatingAvg,
    Integer maxReservationGuestCountPerTeamOnline,
    String contactNumber,
    String address,
    LocalTime openingAt,
    LocalTime closingAt,
    String status,
    String waitingStatus,
    List<Menu> menus,
    List<TimeSlot> timeSlots
){

  public static GetRestaurantResponse from(GetRestaurantInfo restaurant) {
    if (restaurant == null) return null;
    return GetRestaurantResponse.builder()
        .restaurantUuid(restaurant.restaurantUuid())
        .name(restaurant.name())
        .ownerId(restaurant.ownerId())
        .staffId(restaurant.staffId())
        .info(restaurant.info())
        .reviewRatingAvg(restaurant.reviewRatingAvg())
        .maxReservationGuestCountPerTeamOnline(restaurant.maxReservationGuestCountPerTeamOnline())
        .contactNumber(restaurant.contactNumber())
        .address(restaurant.address())
        .openingAt(restaurant.openingAt())
        .closingAt(restaurant.closingAt())
        .status(restaurant.status())
        .waitingStatus(restaurant.waitingStatus())
        .menus(restaurant.menus() != null
            ? restaurant.menus().stream()
            .map(Menu::from)
            .toList()
            : List.of())
        .timeSlots(restaurant.timeSlots() != null
            ? restaurant.timeSlots().stream()
            .map(TimeSlot::from)
            .toList()
            : List.of())
        .build();
  }

  @Builder
  public record Menu(
      String restaurantMenuUuid,
      String name,
      BigDecimal price,
      String status
  ) {
    public static Menu from(GetRestaurantInfo.Menu menu) {
      if (menu == null) return null;
      return Menu.builder()
          .restaurantMenuUuid(menu.restaurantMenuUuid())
          .name(menu.name())
          .price(menu.price())
          .status(menu.status())
          .build();
    }
  }

  @Builder
  public record TimeSlot(
      String restaurantTimeslotUuid,
      LocalDate availableDate,
      LocalTime timeslot,
      Integer maxCapacity,
      Integer curTotalGuestCount
  ) {
    public static TimeSlot from(GetRestaurantInfo.TimeSlot timeSlot) {
      if (timeSlot == null) return null;
      return TimeSlot.builder()
          .restaurantTimeslotUuid(timeSlot.restaurantTimeslotUuid())
          .availableDate(timeSlot.availableDate())
          .timeslot(timeSlot.timeslot())
          .maxCapacity(timeSlot.maxCapacity())
          .curTotalGuestCount(timeSlot.curTotalGuestCount())
          .build();
    }
  }
}
