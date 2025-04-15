package table.eat.now.promotion.promotion.infrastructure.redis;

import table.eat.now.promotion.promotion.infrastructure.dto.request.PromotionUserQuery;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 15.
 */
public interface PromotionRedisRepository {

  void addUserToPromotion(String promotionName, PromotionUserQuery promotionUserQuery);
  void removeUserFromPromotion(String promotionName, Long userId);
}
