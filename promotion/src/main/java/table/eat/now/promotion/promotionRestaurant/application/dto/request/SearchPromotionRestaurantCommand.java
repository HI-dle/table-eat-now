package table.eat.now.promotion.promotionRestaurant.application.dto.request;

import table.eat.now.promotion.promotionRestaurant.domain.repository.search.PromotionRestaurantSearchCriteria;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 08.
 */
public record SearchPromotionRestaurantCommand(String promotionUuid,
                                               String restaurantUuid,
                                               Boolean isAsc,
                                               String sortBy,
                                               int page,
                                               int size) {

  public PromotionRestaurantSearchCriteria toCriteria() {
    return new PromotionRestaurantSearchCriteria(
        promotionUuid,
        restaurantUuid,
        isAsc,
        sortBy,
        page,
        size
    );
  }

}
