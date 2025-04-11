/**
 * @author : jieun(je-pa)
 * @Date : 2025. 04. 11.
 */
package table.eat.now.reservation.reservation.application.client.dto.response;

import java.time.LocalDateTime;
import java.util.Map;

public record GetPromotionsInfo(
    Map<String, Promotion> promotions
) {

  public record Promotion(
      String promotionUuid,
      LocalDateTime startTime,
      LocalDateTime endTime,
      Double discountPrice,
      PromotionStatus promotionStatus,
      String promotionRestaurantUuid
  ) {
    public enum PromotionStatus {
      READY,
      RUNNING,
      CLOSED
    }
  }
}

