package table.eat.now.waiting.waiting_request.infrastructure.client.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import table.eat.now.waiting.waiting_request.application.client.dto.response.GetRestaurantInfo;

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
    String waitingStatus,
    List<Menu> menus,
    List<Timeslot> timeslots
) {

  public GetRestaurantInfo toInfo() {

    return GetRestaurantInfo.builder()
        .id(id)
        .restaurantUuid(restaurantUuid)
        .ownerId(ownerId)
        .staffId(staffId)
        .name(name)
        .address(address)
        .contactNumber(contactNumber)
        .openingAt(openingAt)
        .closingAt(closingAt)
        .reviewRatingAvg(reviewRatingAvg)
        .info(info)
        .status(status)
        .maxReservationGuestCountPerTeam(maxReservationGuestCountPerTeam)
        .waitingStatus(waitingStatus)
        .menus(menus.stream()
            .map(Menu::toInfo)
            .toList())
        .timeslots(timeslots.stream()
            .map(Timeslot::toInfo)
            .toList())
        .build();
  }

  public record Menu(
      String restaurantMenuUuid,
      String name,
      BigDecimal price,
      String status
  ) {

    public GetRestaurantInfo.Menu toInfo() {
      return GetRestaurantInfo.Menu.builder()
          .restaurantMenuUuid(restaurantMenuUuid)
          .name(name)
          .price(price)
          .status(status)
          .build();
    }
  }

  public record Timeslot(
      String restaurantTimeslotUuid,
      LocalDate availableDate,
      Integer maxCapacity,
      Integer curTotalGuestCount,
      LocalTime timeslot
  ) {

    public GetRestaurantInfo.Timeslot toInfo() {
      return GetRestaurantInfo.Timeslot.builder()
          .restaurantTimeslotUuid(restaurantTimeslotUuid)
          .availableDate(availableDate)
          .maxCapacity(maxCapacity)
          .curTotalGuestCount(curTotalGuestCount)
          .timeslot(timeslot)
          .build();
    }
  }
}

