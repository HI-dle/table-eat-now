/**
 * @author : jieun(je-pa)
 * @Date : 2025. 04. 11.
 */
package table.eat.now.reservation.reservation.presentation.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import lombok.Builder;

@Builder
public record GetRestaurantInfo(
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
      BigDecimal price,
      String status
  ) {}

  public record Timeslot(
      String restaurantTimeslotUuid,
      LocalDate availableDate,
      Integer maxCapacity,
      Integer curTotalGuestCount,
      LocalTime timeslot
  ) {}
}
