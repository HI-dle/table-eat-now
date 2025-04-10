/**
 * @author : jieun(je-pa)
 * @Date : 2025. 04. 11.
 */
package table.eat.now.reservation.reservation.infrastructure.client.feign.dto.response;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import table.eat.now.reservation.reservation.application.client.dto.response.GetPromotionsInfo;

public record GetPromotionsResponse(
    List<Promotion> promotions
) {
  public record Promotion(
      Long promotionId,
      String promotionUuid,
      String promotionName,
      String startTime,
      String endTime,
      String description,
      Double discountPrice,
      String promotionStatus,
      String promotionType,
      String promotionRestaurantUuid,
      String restaurantUuid
  ) {
    public GetPromotionsInfo.Promotion toInfoItem() {
      return new GetPromotionsInfo.Promotion(
          promotionId(),
          promotionUuid(),
          promotionName(),
          startTime(),
          endTime(),
          description(),
          discountPrice(),
          promotionStatus(),
          promotionType(),
          promotionRestaurantUuid(),
          restaurantUuid()
      );
    }
  }

  public GetPromotionsInfo toInfo() {
    Map<String, GetPromotionsInfo.Promotion> promotionMap = promotions.stream()
        .map(Promotion::toInfoItem)
        .collect(Collectors.toMap(
            GetPromotionsInfo.Promotion::promotionUuid, promotion -> promotion));

    return new GetPromotionsInfo(promotionMap);
  }
}
