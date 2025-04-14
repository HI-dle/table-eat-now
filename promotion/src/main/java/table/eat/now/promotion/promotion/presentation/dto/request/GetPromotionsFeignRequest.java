package table.eat.now.promotion.promotion.presentation.dto.request;

import java.util.Set;
import table.eat.now.promotion.promotion.application.dto.request.GetPromotionsFeignCommand;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 13.
 */
public record GetPromotionsFeignRequest(Set<String> promotionUuids,
                                        String restaurantUuid) {

  public GetPromotionsFeignCommand toApplication() {
    return new GetPromotionsFeignCommand(
        promotionUuids,
        restaurantUuid
    );
  }

}
