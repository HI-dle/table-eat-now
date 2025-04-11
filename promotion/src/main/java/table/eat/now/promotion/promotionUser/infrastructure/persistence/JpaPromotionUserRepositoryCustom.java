package table.eat.now.promotion.promotionUser.infrastructure.persistence;

import table.eat.now.promotion.promotionUser.domain.repository.search.PaginatedResult;
import table.eat.now.promotion.promotionUser.domain.repository.search.PromotionUserSearchCriteria;
import table.eat.now.promotion.promotionUser.domain.repository.search.PromotionUserSearchCriteriaQuery;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 11.
 */
public interface JpaPromotionUserRepositoryCustom {

  PaginatedResult<PromotionUserSearchCriteriaQuery> searchPromotionUser(
      PromotionUserSearchCriteria criteria);
}
