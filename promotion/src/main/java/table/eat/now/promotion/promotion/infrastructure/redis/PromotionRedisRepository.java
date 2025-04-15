package table.eat.now.promotion.promotion.infrastructure.redis;

import table.eat.now.promotion.promotion.domain.entity.repository.event.PromotionParticipant;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 15.
 */
public interface PromotionRedisRepository {

  boolean addUserToPromotion(String promotionName, PromotionParticipant participant, int maxCount);
}
