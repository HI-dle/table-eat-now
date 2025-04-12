package table.eat.now.promotion.promotion.application.client;

import table.eat.now.promotion.promotion.application.dto.client.response.GetPromotionRestaurantInfo;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 13.
 */
public interface PromotionClient {

  GetPromotionRestaurantInfo findRestaurantsByPromotions(String restaurantUuid);
}
