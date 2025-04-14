package table.eat.now.promotion.promotionrestaurant.presentation.dto.request;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import table.eat.now.promotion.promotionrestaurant.application.dto.request.UpdatePromotionRestaurantCommand;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 08.
 */
public record UpdatePromotionRestaurantRequest(@NotNull
                                               UUID promotionUuid,
                                               @NotNull
                                               UUID restaurantUuid) {

  public UpdatePromotionRestaurantCommand toApplication() {
    return new UpdatePromotionRestaurantCommand(
        promotionUuid.toString(),
        restaurantUuid.toString()
    );
  }

}
