package table.eat.now.promotion.promotionRestaurant.application.dto.response;

import lombok.Builder;
import table.eat.now.promotion.promotionRestaurant.domain.entity.PromotionRestaurant;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 08.
 */
@Builder
public record UpdatePromotionRestaurantInfo(String promotionRestaurantUuid,
                                            String promotionUuid,
                                            String restaurantUuid) {

  public static UpdatePromotionRestaurantInfo from(PromotionRestaurant promotionRestaurant) {
    return UpdatePromotionRestaurantInfo.builder()
        .promotionRestaurantUuid(promotionRestaurant.getPromotionRestaurantUuid())
        .promotionUuid(promotionRestaurant.getPromotionUuid())
        .restaurantUuid(promotionRestaurant.getRestaurantUuid())
        .build();
  }

}
