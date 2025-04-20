package table.eat.now.review.infrastructure.client.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import table.eat.now.review.application.client.dto.GetRestaurantStaffInfo;

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
      BigDecimal price,
      String status
  ) {}

  public record Timeslot(
      String restaurantTimeslotUuid,
      LocalDate availableStartDate,
      Integer maxCapacity,
      Integer curTotalGuestCount,
      LocalTime timeslot
  ) {}

  public GetRestaurantStaffInfo toInfo(){
    return new GetRestaurantStaffInfo(ownerId, staffId);
  }
}