package table.eat.now.promotion.promotion.presentation.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import table.eat.now.promotion.promotion.application.dto.response.GetPromotionsFeignInfo;
import table.eat.now.promotion.promotion.application.dto.response.GetPromotionsFeignInfo.ReservationInfo;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 13.
 */
@Builder
public record GetPromotionsFeignResponse(List<ReservationRequest> reservationRequests
) {

  @Builder
  public record ReservationRequest(Long promotionId,
                                   String promotionUuid,
                                   String promotionName,
                                   LocalDateTime startTime,
                                   LocalDateTime endTime,
                                   String description,
                                   BigDecimal discountPrice,
                                   String promotionStatus,
                                   String promotionType,
                                   String promotionRestaurantUuid,
                                   String restaurantUuid) {

    public static ReservationRequest from(ReservationInfo info) {
      return ReservationRequest.builder()
          .promotionId(info.promotionId())
          .promotionUuid(info.promotionUuid())
          .promotionName(info.promotionName())
          .startTime(info.startTime())
          .endTime(info.endTime())
          .description(info.description())
          .discountPrice(info.discountPrice())
          .promotionStatus(info.promotionStatus())
          .promotionType(info.promotionType())
          .promotionRestaurantUuid(info.promotionRestaurantUuid())
          .restaurantUuid(info.restaurantUuid())
          .build();
    }
  }
  public static GetPromotionsFeignResponse from(GetPromotionsFeignInfo info) {
    List<ReservationRequest> requests = info.reservationRequests().stream()
        .map(ReservationRequest::from)
        .toList();

    return GetPromotionsFeignResponse.builder()
        .reservationRequests(requests)
        .build();
  }
}
