package table.eat.now.promotion.promotionRestaurant.application.dto.request;

import java.util.UUID;
import table.eat.now.promotion.promotionRestaurant.domain.entity.PromotionRestaurant;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 08.
 */
public record CreatePromotionRestaurantCommand(UUID promotionUuid,
                                               UUID restaurantUuid) {

  public PromotionRestaurant toEntity() {
    return PromotionRestaurant.of(
        promotionUuid,
        restaurantUuid
    );
  }

}
