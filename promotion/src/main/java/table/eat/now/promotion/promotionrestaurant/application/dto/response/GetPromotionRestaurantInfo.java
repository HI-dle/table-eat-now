package table.eat.now.promotion.promotionrestaurant.application.dto.response;

import lombok.Builder;
import table.eat.now.promotion.promotionrestaurant.domain.entity.PromotionRestaurant;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 08.
 */
@Builder
public record GetPromotionRestaurantInfo(String promotionRestaurantUuid,
                                         String promotionUuid,
                                         String restaurantUuid) {

  public static GetPromotionRestaurantInfo from(PromotionRestaurant promotionRestaurant) {
    return GetPromotionRestaurantInfo.builder()
        .promotionRestaurantUuid(promotionRestaurant.getPromotionRestaurantUuid())
        .promotionUuid(promotionRestaurant.getPromotionUuid())
        .restaurantUuid(promotionRestaurant.getRestaurantUuid())
        .build();
  }

}
