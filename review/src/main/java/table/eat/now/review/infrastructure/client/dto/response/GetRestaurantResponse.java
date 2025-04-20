package table.eat.now.review.infrastructure.client.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import lombok.Builder;
import table.eat.now.review.application.client.dto.GetRestaurantStaffInfo;

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
  public record Menu(
      String restaurantMenuUuid,
      String name,
      BigDecimal price,
      String status
  ) {
  }
  public record TimeSlot(
      String restaurantTimeslotUuid,
      LocalDate availableDate,
      LocalTime timeslot,
      Integer maxCapacity,
      Integer curTotalGuestCount
  ) {
  }

  public GetRestaurantStaffInfo toInfo(){
    return new GetRestaurantStaffInfo(ownerId, staffId);
  }
}