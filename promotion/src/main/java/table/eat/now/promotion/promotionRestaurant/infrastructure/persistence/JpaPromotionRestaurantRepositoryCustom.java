package table.eat.now.promotion.promotionRestaurant.infrastructure.persistence;

import table.eat.now.promotion.promotionRestaurant.domain.repository.search.PaginatedResult;
import table.eat.now.promotion.promotionRestaurant.domain.repository.search.PromotionRestaurantSearchCriteria;
import table.eat.now.promotion.promotionRestaurant.domain.repository.search.PromotionRestaurantSearchCriteriaQuery;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 11.
 */
public interface JpaPromotionRestaurantRepositoryCustom {

  PaginatedResult<PromotionRestaurantSearchCriteriaQuery> searchPromotionRestaurant(
      PromotionRestaurantSearchCriteria criteria);

}
