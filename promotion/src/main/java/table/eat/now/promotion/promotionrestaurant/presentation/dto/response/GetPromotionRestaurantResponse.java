package table.eat.now.promotion.promotionrestaurant.presentation.dto.response;

import lombok.Builder;
import table.eat.now.promotion.promotionrestaurant.application.dto.response.GetPromotionRestaurantInfo;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 08.
 */
@Builder
public record GetPromotionRestaurantResponse(String promotionRestaurantUuid,
                                             String promotionUuid,
                                             String restaurantUuid) {

  public static GetPromotionRestaurantResponse from(GetPromotionRestaurantInfo info) {
    return GetPromotionRestaurantResponse.builder()
        .promotionRestaurantUuid(info.promotionRestaurantUuid())
        .promotionUuid(info.promotionUuid())
        .restaurantUuid(info.restaurantUuid())
        .build();
  }

}
