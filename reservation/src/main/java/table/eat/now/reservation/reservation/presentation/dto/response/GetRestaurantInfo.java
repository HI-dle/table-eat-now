package table.eat.now.reservation.reservation.presentation.dto.response;

import java.util.List;

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
      Integer price,
      String status
  ) {}

  public record Timeslot(
      String restaurantTimeslotUuid,
      String availableStartDate,
      Integer maxCapacity,
      String timeslot
  ) {}
}
