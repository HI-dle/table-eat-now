package table.eat.now.promotion.promotionuser.application.dto.request;

import table.eat.now.promotion.promotionuser.domain.entity.PromotionUser;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 08.
 */
public record UpdatePromotionUserCommand(Long userId,
                                         String promotionUuid) {

  public PromotionUser toEntity() {
    return PromotionUser.of(userId, promotionUuid);
  }
}
