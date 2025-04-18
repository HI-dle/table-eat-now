/**
 * @author : jieun(je-pa)
 * @Date : 2025. 04. 18.
 */
package table.eat.now.restaurant.restaurant.application.service.dto.request;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import lombok.Builder;
import table.eat.now.common.resolver.dto.UserRole;
import table.eat.now.restaurant.restaurant.domain.entity.RestaurantMenu;
import table.eat.now.restaurant.restaurant.domain.entity.RestaurantMenu.MenuStatus;
import table.eat.now.restaurant.restaurant.domain.entity.RestaurantTimeSlot;

@Builder
public record ModifyRestaurantCommand(
    Long requesterId,
    UserRole requesterRole,
    String restaurantUuid,
    String name,
    String info,
    Integer maxReservationGuestCountPerTeamOnline,
    String waitingStatus,
    String status,
    String contactNumber,
    String address,
    LocalTime openingAt,
    LocalTime closingAt,
    List<MenuCommand> menus,
    List<TimeSlotCommand> timeslots
) {
  @Builder
  public record MenuCommand(
      String restaurantMenuUuid,
      String name,
      BigDecimal price,
      String status
  ) {

    public RestaurantMenu toNewEntity(String newRestaurantMenuUuid) {
      return RestaurantMenu.fullBuilder()
          .restaurantMenuUuid(newRestaurantMenuUuid)
          .name(name)
          .price(price)
          .menuStatus(MenuStatus.valueOf(status))
          .build();
    }
  }

  @Builder
  public record TimeSlotCommand(
      String restaurantTimeslotUuid,
      LocalDate availableDate,
      LocalTime timeslot,
      Integer maxCapacity
  ) {

    public RestaurantTimeSlot toNewEntity(String newTimeSlotUuid) {
      return RestaurantTimeSlot.baseBuilder()
          .restaurantTimeslotUuid(newTimeSlotUuid)
          .availableDate(availableDate)
          .timeslot(timeslot)
          .maxCapacity(maxCapacity)
          .build();
    }
  }
}