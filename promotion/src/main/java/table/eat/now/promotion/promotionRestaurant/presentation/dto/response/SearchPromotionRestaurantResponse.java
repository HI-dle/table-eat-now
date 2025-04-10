package table.eat.now.promotion.promotionRestaurant.presentation.dto.response;

import lombok.Builder;
import table.eat.now.promotion.promotionRestaurant.application.dto.response.CreatePromotionRestaurantInfo;
import table.eat.now.promotion.promotionRestaurant.application.dto.response.SearchPromotionRestaurantInfo;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 08.
 */
@Builder
public record SearchPromotionRestaurantResponse(String promotionRestaurantUuid,
                                                String promotionUuid,
                                                String restaurantUuid) {

  public static SearchPromotionRestaurantResponse from(SearchPromotionRestaurantInfo info) {
    return SearchPromotionRestaurantResponse.builder()
        .promotionRestaurantUuid(info.promotionRestaurantUuid())
        .promotionUuid(info.promotionUuid())
        .restaurantUuid(info.restaurantUuid())
        .build();
  }

}
