package table.eat.now.promotion.promotionuser.presentation.dto.response;

import lombok.Builder;
import table.eat.now.promotion.promotionuser.application.dto.response.UpdatePromotionUserInfo;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 08.
 */
@Builder
public record UpdatePromotionUserResponse(String promotionUserUuid,
                                          Long userId,
                                          String promotionUuid) {

  public static UpdatePromotionUserResponse from(UpdatePromotionUserInfo info) {
    return UpdatePromotionUserResponse.builder()
        .promotionUserUuid(info.promotionUserUuid())
        .userId(info.userId())
        .promotionUuid(info.promotionUuid())
        .build();
  }

}
