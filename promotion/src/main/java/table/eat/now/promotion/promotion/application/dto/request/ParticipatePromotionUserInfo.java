package table.eat.now.promotion.promotion.application.dto.request;

import table.eat.now.promotion.promotion.domain.entity.repository.event.PromotionParticipant;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 15.
 */
public record ParticipatePromotionUserInfo(Long userId,
                                           String promotionUuid,
                                           String promotionName) {

  public PromotionParticipant toDomain() {
    return new PromotionParticipant(
        userId,
        promotionUuid,
        promotionName
    );
  }

}
