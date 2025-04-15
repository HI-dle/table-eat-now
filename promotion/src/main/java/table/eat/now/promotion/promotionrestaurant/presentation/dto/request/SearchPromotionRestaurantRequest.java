package table.eat.now.promotion.promotionrestaurant.presentation.dto.request;

import java.util.UUID;
import table.eat.now.promotion.promotionrestaurant.application.dto.request.SearchPromotionRestaurantCommand;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 08.
 */
public record SearchPromotionRestaurantRequest(UUID promotionUuid,
                                               UUID restaurantUuid,
                                               Boolean isAsc,
                                               String sortBy,
                                               int page,
                                               int size) {

  public SearchPromotionRestaurantCommand toApplication() {
    return new SearchPromotionRestaurantCommand(
        promotionUuid.toString(),
        restaurantUuid.toString(),
        isAsc,
        sortBy,
        page,
        size
    );
  }

}
