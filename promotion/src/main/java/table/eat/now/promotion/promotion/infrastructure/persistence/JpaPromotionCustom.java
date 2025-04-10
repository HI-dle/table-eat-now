package table.eat.now.promotion.promotion.infrastructure.persistence;

import table.eat.now.promotion.promotion.domain.entity.Promotion;
import table.eat.now.promotion.promotion.domain.entity.repository.search.PaginatedResult;
import table.eat.now.promotion.promotion.domain.entity.repository.search.PromotionSearchCriteria;
import table.eat.now.promotion.promotion.domain.entity.repository.search.PromotionSearchCriteriaQuery;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 10.
 */
public interface JpaPromotionCustom {
  PaginatedResult<PromotionSearchCriteriaQuery> searchPromotion(PromotionSearchCriteria criteria);

}
