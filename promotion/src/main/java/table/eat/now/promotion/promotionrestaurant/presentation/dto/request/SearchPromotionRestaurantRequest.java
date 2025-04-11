package table.eat.now.promotion.promotionrestaurant.presentation.dto.request;

import table.eat.now.promotion.promotionrestaurant.application.dto.request.SearchPromotionRestaurantCommand;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 08.
 */
public record SearchPromotionRestaurantRequest(String promotionUuid,
                                               String restaurantUuid,
                                               Boolean isAsc,
                                               String sortBy,
                                               int page,
                                               int size) {

  public SearchPromotionRestaurantCommand toApplication() {
    return new SearchPromotionRestaurantCommand(
        promotionUuid,
        restaurantUuid,
        isAsc,
        sortBy,
        page,
        size
    );
  }

}
