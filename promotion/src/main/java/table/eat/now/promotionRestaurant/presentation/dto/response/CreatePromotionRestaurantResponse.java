package table.eat.now.promotionRestaurant.presentation.dto.response;

import java.util.UUID;
import lombok.Builder;
import table.eat.now.promotionRestaurant.application.dto.response.CreatePromotionRestaurantInfo;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 08.
 */
@Builder
public record CreatePromotionRestaurantResponse(UUID promotionRestaurantUuid,
                                                UUID promotionUuid,
                                                UUID restaurantUuid) {

  public static CreatePromotionRestaurantResponse from(CreatePromotionRestaurantInfo info) {
    return CreatePromotionRestaurantResponse.builder()
        .promotionRestaurantUuid(info.restaurantUuid())
        .promotionUuid(info.promotionUuid())
        .restaurantUuid(info.restaurantUuid())
        .build();
  }

}
