package table.eat.now.promotion.promotionrestaurant.application.dto.response;

import lombok.Builder;
import table.eat.now.promotion.promotionrestaurant.domain.entity.PromotionRestaurant;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 08.
 */
@Builder
public record CreatePromotionRestaurantInfo(String promotionRestaurantUuid,
                                            String promotionUuid,
                                            String restaurantUuid) {

  public static CreatePromotionRestaurantInfo from(PromotionRestaurant promotionRestaurant) {
    return CreatePromotionRestaurantInfo.builder()
        .promotionRestaurantUuid(promotionRestaurant.getPromotionRestaurantUuid())
        .promotionUuid(promotionRestaurant.getPromotionUuid())
        .restaurantUuid(promotionRestaurant.getRestaurantUuid())
        .build();
  }

}
