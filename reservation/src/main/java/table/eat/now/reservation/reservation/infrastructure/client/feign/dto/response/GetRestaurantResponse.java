/**
 * @author : jieun(je-pa)
 * @Date : 2025. 04. 11.
 */
package table.eat.now.reservation.reservation.infrastructure.client.feign.dto.response;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import table.eat.now.reservation.reservation.presentation.dto.response.GetRestaurantInfo;

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
    List<Timeslot> timeslots
) {

  public record Menu(
      String restaurantMenuUuid,
      String name,
      Integer price,
      String status
  ) {}

  public record Timeslot(
      String restaurantTimeslotUuid,
      LocalDate availableStartDate,
      Integer maxCapacity,
      Integer curTotalGuestCount,
      LocalTime timeslot
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

    List<GetRestaurantInfo.Timeslot> infoTimeslots = timeslots.stream()
        .map(timeslot -> new GetRestaurantInfo.Timeslot(
            timeslot.restaurantTimeslotUuid(),
            timeslot.availableStartDate(),
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

