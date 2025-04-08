package table.eat.now.promotion.promotionRestaurant.presentation.dto.request;

import java.util.UUID;
import table.eat.now.promotion.promotionRestaurant.application.dto.request.CreatePromotionRestaurantCommand;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 08.
 */
public record CreatePromotionRestaurantRequest(UUID promotionUuid,
                                               UUID restaurantUuid) {

  public CreatePromotionRestaurantCommand toApplication() {
    return new CreatePromotionRestaurantCommand(
        promotionUuid,
        restaurantUuid
    );
  }

}
