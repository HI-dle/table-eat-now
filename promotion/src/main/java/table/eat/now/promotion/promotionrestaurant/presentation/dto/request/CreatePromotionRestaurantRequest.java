package table.eat.now.promotion.promotionrestaurant.presentation.dto.request;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import table.eat.now.promotion.promotionrestaurant.application.dto.request.CreatePromotionRestaurantCommand;

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
        promotionUuid.toString(),
        restaurantUuid.toString()
    );
  }

}
