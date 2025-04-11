/**
 * @author : jieun(je-pa)
 * @Date : 2025. 04. 11.
 */
package table.eat.now.reservation.reservation.application.client.dto.request;

import java.util.Set;

public record GetPromotionsCriteria(
    Set<String> promotionUuids,
    String restaurantUuid
) {

  public static GetPromotionsCriteria of(Set<String> promotionIds, String restaurantUuid) {
    return new GetPromotionsCriteria(promotionIds, restaurantUuid);
  }
}
