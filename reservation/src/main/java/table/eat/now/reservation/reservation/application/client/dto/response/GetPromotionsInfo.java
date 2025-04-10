/**
 * @author : jieun(je-pa)
 * @Date : 2025. 04. 11.
 */
package table.eat.now.reservation.reservation.application.client.dto.response;

import java.util.Map;

public record GetPromotionsInfo(
    Map<String, Promotion> promotions
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

  }
}

