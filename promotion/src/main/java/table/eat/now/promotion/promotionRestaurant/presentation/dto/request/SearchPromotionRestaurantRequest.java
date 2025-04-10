package table.eat.now.promotion.promotionRestaurant.presentation.dto.request;

import jakarta.validation.constraints.NotNull;
import table.eat.now.promotion.promotionRestaurant.application.dto.request.CreatePromotionRestaurantCommand;
import table.eat.now.promotion.promotionRestaurant.application.dto.request.SearchPromotionRestaurantCommand;

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
