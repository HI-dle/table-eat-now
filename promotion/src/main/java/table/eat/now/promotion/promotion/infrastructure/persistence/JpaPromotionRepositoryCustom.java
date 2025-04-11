package table.eat.now.promotion.promotion.infrastructure.persistence;

import table.eat.now.promotion.promotion.domain.entity.repository.search.PaginatedResult;
import table.eat.now.promotion.promotion.domain.entity.repository.search.PromotionSearchCriteria;
import table.eat.now.promotion.promotion.domain.entity.repository.search.PromotionSearchCriteriaQuery;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 10.
 */
public interface JpaPromotionRepositoryCustom {
  PaginatedResult<PromotionSearchCriteriaQuery> searchPromotion(PromotionSearchCriteria criteria);

}
