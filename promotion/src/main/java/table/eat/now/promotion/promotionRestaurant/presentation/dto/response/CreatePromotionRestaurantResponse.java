package table.eat.now.promotion.promotionRestaurant.presentation.dto.response;

import lombok.Builder;
import table.eat.now.promotion.promotionRestaurant.application.dto.response.CreatePromotionRestaurantInfo;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 08.
 */
@Builder
public record CreatePromotionRestaurantResponse(String promotionRestaurantUuid,
                                                String promotionUuid,
                                                String restaurantUuid) {

  public static CreatePromotionRestaurantResponse from(CreatePromotionRestaurantInfo info) {
    return CreatePromotionRestaurantResponse.builder()
        .promotionRestaurantUuid(info.promotionRestaurantUuid())
        .promotionUuid(info.promotionUuid())
        .restaurantUuid(info.restaurantUuid())
        .build();
  }

}
