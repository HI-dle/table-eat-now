package table.eat.now.promotion.promotion.infrastructure.redis;

import java.util.List;
import table.eat.now.promotion.promotion.domain.entity.repository.event.ParticipateResult;
import table.eat.now.promotion.promotion.domain.entity.repository.event.PromotionParticipant;
import table.eat.now.promotion.promotion.domain.entity.repository.event.PromotionParticipantDto;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 15.
 */
public interface PromotionRedisRepository {

  ParticipateResult addUserToPromotion(PromotionParticipant participant, int maxCount);
  List<PromotionParticipantDto> getPromotionUsers(String promotionName);
}
