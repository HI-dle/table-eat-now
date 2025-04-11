/**
 * @author : jieun(je-pa)
 * @Date : 2025. 04. 11.
 */
package table.eat.now.reservation.reservation.application.client.dto.response;

import java.math.BigDecimal;
import java.util.Map;
import lombok.Builder;

public record GetPromotionsInfo(
    Map<String, Promotion> promotions
) {

  @Builder
  public record Promotion(
      String promotionUuid,
      BigDecimal discountPrice,
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

