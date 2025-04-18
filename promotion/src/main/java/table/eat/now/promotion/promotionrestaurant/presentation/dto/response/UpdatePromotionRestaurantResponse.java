package table.eat.now.promotion.promotionrestaurant.presentation.dto.response;

import lombok.Builder;
import table.eat.now.promotion.promotionrestaurant.application.dto.response.UpdatePromotionRestaurantInfo;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 08.
 */
@Builder
public record UpdatePromotionRestaurantResponse(String promotionRestaurantUuid,
                                                String promotionUuid,
                                                String restaurantUuid) {

  public static UpdatePromotionRestaurantResponse from(UpdatePromotionRestaurantInfo info) {
    return UpdatePromotionRestaurantResponse.builder()
        .promotionRestaurantUuid(info.promotionRestaurantUuid())
        .promotionUuid(info.promotionUuid())
        .restaurantUuid(info.restaurantUuid())
        .build();
  }

}
