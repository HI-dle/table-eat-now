package table.eat.now.promotion.promotionUser.application.dto.request;

import table.eat.now.promotion.promotionUser.domain.entity.PromotionUser;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 08.
 */
public record CreatePromotionUserCommand(Long userId,
                                         String promotionUuid) {

  public PromotionUser toEntity() {
    return PromotionUser.of(userId,promotionUuid);
  }
}
