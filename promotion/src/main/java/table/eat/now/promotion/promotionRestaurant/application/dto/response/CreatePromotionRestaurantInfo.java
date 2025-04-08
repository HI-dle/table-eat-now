package table.eat.now.promotion.promotionRestaurant.application.dto.response;

import java.util.UUID;
import lombok.Builder;
import table.eat.now.promotion.promotionRestaurant.domain.entity.PromotionRestaurant;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 08.
 */
@Builder
public record CreatePromotionRestaurantInfo(UUID promotionRestaurantUuid,
                                            UUID promotionUuid,
                                            UUID restaurantUuid) {

  public static CreatePromotionRestaurantInfo from(PromotionRestaurant promotionRestaurant) {
    return CreatePromotionRestaurantInfo.builder()
        .promotionRestaurantUuid(promotionRestaurant.getPromotionRestaurantUuid())
        .promotionUuid(promotionRestaurant.getPromotionUuid())
        .restaurantUuid(promotionRestaurant.getRestaurantUuid())
        .build();
  }

}
