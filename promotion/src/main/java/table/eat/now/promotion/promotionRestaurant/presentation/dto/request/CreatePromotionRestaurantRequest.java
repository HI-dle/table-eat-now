package table.eat.now.promotion.promotionRestaurant.presentation.dto.request;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import table.eat.now.promotion.promotionRestaurant.application.dto.request.CreatePromotionRestaurantCommand;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 08.
 */
public record CreatePromotionRestaurantRequest(@NotNull
                                               UUID promotionUuid,
                                               @NotNull
                                               UUID restaurantUuid) {

  public CreatePromotionRestaurantCommand toApplication() {
    return new CreatePromotionRestaurantCommand(
        promotionUuid,
        restaurantUuid
    );
  }

}
