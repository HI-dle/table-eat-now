package table.eat.now.promotion.promotionrestaurant.application.dto.request;

import table.eat.now.promotion.promotionrestaurant.domain.entity.PromotionRestaurant;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 08.
 */
public record UpdatePromotionRestaurantCommand(String promotionUuid,
                                               String restaurantUuid) {

  public PromotionRestaurant toEntity() {
    return PromotionRestaurant.of(
        promotionUuid,
        restaurantUuid
    );
  }

}
