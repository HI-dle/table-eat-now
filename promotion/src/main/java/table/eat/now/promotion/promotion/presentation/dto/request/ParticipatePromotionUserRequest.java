package table.eat.now.promotion.promotion.presentation.dto.request;

import table.eat.now.promotion.promotion.application.dto.request.ParticipatePromotionUserInfo;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 15.
 */
public record ParticipatePromotionUserRequest(Long userId,
                                              String promotionUuid,
                                              String promotionName) {

  public ParticipatePromotionUserInfo toApplication() {
    return new ParticipatePromotionUserInfo(
        userId,
        promotionUuid,
        promotionName
    );
  }

}
