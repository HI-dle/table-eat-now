/**
 * @author : jieun(je-pa)
 * @Date : 2025. 04. 11.
 */
package table.eat.now.reservation.reservation.infrastructure.client.feign.dto.request;

import java.util.Set;
import table.eat.now.reservation.reservation.application.client.dto.request.GetPromotionsCriteria;

public record GetPromotionsRequest(
    Set<String> promotionUuids,
    String restaurantUuid
) {
  public static GetPromotionsRequest from(GetPromotionsCriteria criteria) {
    return new GetPromotionsRequest(criteria.promotionUuids(), criteria.restaurantUuid());
  }
}
