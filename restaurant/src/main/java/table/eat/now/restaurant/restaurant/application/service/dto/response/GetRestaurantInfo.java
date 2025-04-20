/**
 * @author : jieun(je-pa)
 * @Date : 2025. 04. 15.
 */
package table.eat.now.restaurant.restaurant.application.service.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import lombok.Builder;
import table.eat.now.restaurant.restaurant.domain.entity.Restaurant;
import table.eat.now.restaurant.restaurant.domain.entity.RestaurantMenu;
import table.eat.now.restaurant.restaurant.domain.entity.RestaurantTimeSlot;

@Builder
public record GetRestaurantInfo(
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
) {

  public static GetRestaurantInfo from(Restaurant restaurant) {
    if (restaurant == null) return null;
    return GetRestaurantInfo.builder()
        .restaurantUuid(restaurant.getRestaurantUuid())
        .name(restaurant.getName())
        .ownerId(restaurant.getOwnerId())
        .staffId(restaurant.getStaffId())
        .info(restaurant.getInfo())
        .reviewRatingAvg(restaurant.getReviewRatingAvg())
        .maxReservationGuestCountPerTeamOnline(restaurant.getMaxReservationGuestCountPerTeamOnline())
        .contactNumber(restaurant.getContactInfo().getContactNumber())
        .address(restaurant.getContactInfo().getAddress())
        .openingAt(restaurant.getOperatingTime().getOpeningAt())
        .closingAt(restaurant.getOperatingTime().getClosingAt())
        .status(restaurant.getStatus().name())
        .waitingStatus(restaurant.getWaitingStatus().name())
        .menus(restaurant.getMenus() != null
            ? restaurant.getMenus().stream()
            .map(Menu::from)
            .toList()
            : List.of())
        .timeSlots(restaurant.getTimeSlots() != null
            ? restaurant.getTimeSlots().stream()
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
    public static Menu from(RestaurantMenu menu) {
      if (menu == null) return null;
      return Menu.builder()
          .restaurantMenuUuid(menu.getRestaurantMenuUuid())
          .name(menu.getName())
          .price(menu.getPrice())
          .status(menu.getStatus().name())
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
    public static TimeSlot from(RestaurantTimeSlot timeSlot) {
      if (timeSlot == null) return null;
      return TimeSlot.builder()
          .restaurantTimeslotUuid(timeSlot.getRestaurantTimeslotUuid())
          .availableDate(timeSlot.getAvailableDate())
          .timeslot(timeSlot.getTimeslot())
          .maxCapacity(timeSlot.getMaxCapacity())
          .curTotalGuestCount(timeSlot.getCurTotalGuestCount())
          .build();
    }
  }
}
