package table.eat.now.promotion.promotionRestaurant.domain.repository;

import java.util.Optional;
import table.eat.now.promotion.promotionRestaurant.domain.entity.PromotionRestaurant;
import table.eat.now.promotion.promotionRestaurant.domain.repository.search.PaginatedResult;
import table.eat.now.promotion.promotionRestaurant.domain.repository.search.PromotionRestaurantSearchCriteria;
import table.eat.now.promotion.promotionRestaurant.domain.repository.search.PromotionRestaurantSearchCriteriaQuery;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 08.
 */
public interface PromotionRestaurantRepository {

  PromotionRestaurant save(PromotionRestaurant promotionRestaurant);
  Optional<PromotionRestaurant> findByPromotionRestaurantUuidAndDeletedAtIsNull(
      String promotionRestaurantUuid);

  PaginatedResult<PromotionRestaurantSearchCriteriaQuery> searchPromotionRestaurant(
      PromotionRestaurantSearchCriteria criteria);
  Optional<PromotionRestaurant> findByRestaurantUuidAAndDeletedAtIsNull(
      String restaurantUuid);
}
