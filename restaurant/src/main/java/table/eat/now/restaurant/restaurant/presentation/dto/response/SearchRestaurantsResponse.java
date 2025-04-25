package table.eat.now.restaurant.restaurant.presentation.dto.response;

import java.math.BigDecimal;
import java.time.LocalTime;
import lombok.Builder;
import table.eat.now.restaurant.restaurant.application.service.dto.response.SearchRestaurantsInfo;

@Builder
public record SearchRestaurantsResponse(
    String restaurantUuid,
    Long ownerId,
    Long staffId,
    String name,
    BigDecimal reviewRatingAvg,
    String info,
    Integer maxReservationGuestCountPerTeamOnline,
    String waitingStatus,
    String status,
    String contactNumber,
    String address,
    LocalTime openingAt,
    LocalTime closingAt
) {
  public static SearchRestaurantsResponse from(SearchRestaurantsInfo info){
    return SearchRestaurantsResponse.builder()
        .restaurantUuid(info.restaurantUuid())
        .ownerId(info.ownerId())
        .staffId(info.staffId())
        .name(info.name())
        .reviewRatingAvg(info.reviewRatingAvg())
        .info(info.info())
        .maxReservationGuestCountPerTeamOnline(info.maxReservationGuestCountPerTeamOnline())
        .waitingStatus(info.waitingStatus())
        .status(info.status())
        .contactNumber(info.contactNumber())
        .address(info.address())
        .openingAt(info.openingAt())
        .closingAt(info.closingAt())
        .build();
  }
}
