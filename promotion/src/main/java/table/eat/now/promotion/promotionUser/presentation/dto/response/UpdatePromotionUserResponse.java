package table.eat.now.promotion.promotionUser.presentation.dto.response;

import lombok.Builder;
import table.eat.now.promotion.promotionUser.application.dto.response.CreatePromotionUserInfo;
import table.eat.now.promotion.promotionUser.application.dto.response.UpdatePromotionUserInfo;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 08.
 */
@Builder
public record UpdatePromotionUserResponse(String promotionUserUuid,
                                          Long userId) {

  public static UpdatePromotionUserResponse from(UpdatePromotionUserInfo info) {
    return UpdatePromotionUserResponse.builder()
        .promotionUserUuid(info.promotionUserUuid())
        .userId(info.userId())
        .build();
  }

}
