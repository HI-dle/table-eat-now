package table.eat.now.promotion.promotionUser.domain.repository;

import java.util.Optional;
import table.eat.now.promotion.promotionUser.domain.entity.PromotionUser;
import table.eat.now.promotion.promotionUser.domain.repository.search.PaginatedResult;
import table.eat.now.promotion.promotionUser.domain.repository.search.PromotionUserSearchCriteria;
import table.eat.now.promotion.promotionUser.domain.repository.search.PromotionUserSearchCriteriaQuery;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 08.
 */
public interface PromotionUserRepository {

  PromotionUser save(PromotionUser promotionUser);

  Optional<PromotionUser> findByPromotionUserUuidAndDeletedAtIsNull(
      String promotionUserUuid);

  PaginatedResult<PromotionUserSearchCriteriaQuery> searchPromotionUser(PromotionUserSearchCriteria criteria);

  Optional<PromotionUser> findByUserIdAndDeletedAtIsNull(Long userId);
}
