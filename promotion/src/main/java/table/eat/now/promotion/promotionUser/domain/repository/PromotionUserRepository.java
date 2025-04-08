package table.eat.now.promotion.promotionUser.domain.repository;

import table.eat.now.promotion.promotionUser.domain.entity.PromotionUser;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 08.
 */
public interface PromotionUserRepository {

  PromotionUser save(PromotionUser promotionUser);
}
