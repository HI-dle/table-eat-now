package table.eat.now.promotion.promotionUser.application.dto.response;

import lombok.Builder;
import table.eat.now.promotion.promotionUser.domain.entity.PromotionUser;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 08.
 */
@Builder
public record CreatePromotionUserInfo(String promotionUserUuid,
                                      Long userId) {

  public static CreatePromotionUserInfo from(PromotionUser promotionUser) {
    return CreatePromotionUserInfo.builder()
        .promotionUserUuid(promotionUser.getPromotionUserUuid())
        .userId(promotionUser.getUserId())
        .build();
  }
}
