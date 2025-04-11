package table.eat.now.promotion.promotionrestaurant.application.dto.response;

import lombok.Builder;
import table.eat.now.promotion.promotionrestaurant.domain.repository.search.PromotionRestaurantSearchCriteriaQuery;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 08.
 */
@Builder
public record SearchPromotionRestaurantInfo(String promotionRestaurantUuid,
                                            String promotionUuid,
                                            String restaurantUuid) {

  public static SearchPromotionRestaurantInfo from(PromotionRestaurantSearchCriteriaQuery query) {
    return SearchPromotionRestaurantInfo.builder()
        .promotionRestaurantUuid(query.promotionRestaurantUuid())
        .promotionUuid(query.promotionRestaurantUuid())
        .restaurantUuid(query.promotionRestaurantUuid())
        .build();
  }

}
