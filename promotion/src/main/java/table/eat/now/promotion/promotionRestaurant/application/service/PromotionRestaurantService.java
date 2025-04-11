package table.eat.now.promotion.promotionRestaurant.application.service;

import table.eat.now.common.resolver.dto.CurrentUserInfoDto;
import table.eat.now.promotion.promotionRestaurant.application.dto.PaginatedResultCommand;
import table.eat.now.promotion.promotionRestaurant.application.dto.request.CreatePromotionRestaurantCommand;
import table.eat.now.promotion.promotionRestaurant.application.dto.request.SearchPromotionRestaurantCommand;
import table.eat.now.promotion.promotionRestaurant.application.dto.request.UpdatePromotionRestaurantCommand;
import table.eat.now.promotion.promotionRestaurant.application.dto.response.CreatePromotionRestaurantInfo;
import table.eat.now.promotion.promotionRestaurant.application.dto.response.SearchPromotionRestaurantInfo;
import table.eat.now.promotion.promotionRestaurant.application.dto.response.UpdatePromotionRestaurantInfo;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 08.
 */
public interface PromotionRestaurantService {

  CreatePromotionRestaurantInfo createPromotionRestaurant(CreatePromotionRestaurantCommand command);

  UpdatePromotionRestaurantInfo updatePromotionRestaurant(
      UpdatePromotionRestaurantCommand command, String promotionRestaurantUuid);

  PaginatedResultCommand<SearchPromotionRestaurantInfo> searchPromotionRestaurant(
      SearchPromotionRestaurantCommand info);

  void deletePromotionRestaurant(String restaurantUuid, CurrentUserInfoDto userInfoDto);
}
