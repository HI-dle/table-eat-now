/**
 * @author : jieun(je-pa)
 * @Date : 2025. 04. 11.
 */
package table.eat.now.reservation.reservation.infrastructure.client.feign.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import table.eat.now.reservation.reservation.application.service.dto.response.GetRestaurantInfo;

public record GetRestaurantResponse(
    Long id,
    String restaurantUuid,
    Long ownerId,
    Long staffId,
    String name,
    String address,
    String contactNumber,
    String openingAt,
    String closingAt,
    Double reviewRatingAvg,
    String info,
    String status,
    Integer maxReservationGuestCountPerTeam,
    String waiting_status,
    List<Menu> menus,
    List<Timeslot> timeSlots
) {

  public record Menu(
      String restaurantMenuUuid,
      String name,
      BigDecimal price,
      String status
  ) {}

  public record Timeslot(
      String restaurantTimeslotUuid,
      LocalDate availableDate,
      LocalTime timeslot,
      Integer maxCapacity,
      Integer curTotalGuestCount
  ) {}

  public GetRestaurantInfo toInfo() {
    List<GetRestaurantInfo.Menu> infoMenus = menus.stream()
        .map(menu -> new GetRestaurantInfo.Menu(
            menu.restaurantMenuUuid(),
            menu.name(),
            menu.price(),
            menu.status()
        ))
        .toList();

    List<GetRestaurantInfo.Timeslot> infoTimeslots = timeSlots.stream()
        .map(timeslot -> new GetRestaurantInfo.Timeslot(
            timeslot.restaurantTimeslotUuid(),
            timeslot.availableDate(),
            timeslot.maxCapacity(),
            timeslot.curTotalGuestCount(),
            timeslot.timeslot()
        ))
        .toList();

    return new GetRestaurantInfo(
        id,
        restaurantUuid,
        ownerId,
        staffId,
        name,
        address,
        contactNumber,
        openingAt,
        closingAt,
        reviewRatingAvg,
        info,
        status,
        maxReservationGuestCountPerTeam,
        waiting_status,
        infoMenus,
        infoTimeslots
    );
  }
}

