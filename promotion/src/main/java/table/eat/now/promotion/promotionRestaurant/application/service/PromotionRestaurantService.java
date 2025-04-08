package table.eat.now.promotion.promotionRestaurant.application.service;

import table.eat.now.promotion.promotionRestaurant.application.dto.request.CreatePromotionRestaurantCommand;
import table.eat.now.promotion.promotionRestaurant.application.dto.response.CreatePromotionRestaurantInfo;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 08.
 */
public interface PromotionRestaurantService {

  CreatePromotionRestaurantInfo createPromotionRestaurant(CreatePromotionRestaurantCommand command);

}
