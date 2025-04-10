package table.eat.now.promotion.promotionRestaurant.presentation.dto.request;

import jakarta.validation.constraints.NotNull;
import table.eat.now.promotion.promotionRestaurant.application.dto.request.UpdatePromotionRestaurantCommand;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 08.
 */
public record UpdatePromotionRestaurantRequest(@NotNull
                                               String promotionUuid,
                                               @NotNull
                                               String restaurantUuid) {

  public UpdatePromotionRestaurantCommand toApplication() {
    return new UpdatePromotionRestaurantCommand(
        promotionUuid,
        restaurantUuid
    );
  }

}
