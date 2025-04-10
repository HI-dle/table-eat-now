package table.eat.now.promotion.promotionRestaurant.domain.repository.search;

import lombok.Builder;
import table.eat.now.promotion.promotionRestaurant.domain.entity.PromotionRestaurant;


/**
 * @author : hanjihoon
 * @Date : 2025. 04. 09.
 */
@Builder
public record PromotionRestaurantSearchCriteriaQuery(String promotionRestaurantUuid,
                                                     String promotionUuid,
                                                     String restaurantUuid) {

  public static PromotionRestaurantSearchCriteriaQuery from(PromotionRestaurant promotionRestaurant) {
    return PromotionRestaurantSearchCriteriaQuery.builder()

        .build();
  }

}
