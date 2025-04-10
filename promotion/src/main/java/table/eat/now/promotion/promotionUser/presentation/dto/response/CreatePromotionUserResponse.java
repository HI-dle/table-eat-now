package table.eat.now.promotion.promotionUser.presentation.dto.response;

import lombok.Builder;
import table.eat.now.promotion.promotionUser.application.dto.response.CreatePromotionUserInfo;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 08.
 */
@Builder
public record CreatePromotionUserResponse(String promotionUserUuid,
                                          Long userId) {

  public static CreatePromotionUserResponse from(CreatePromotionUserInfo info) {
    return CreatePromotionUserResponse.builder()
        .promotionUserUuid(info.promotionUserUuid())
        .userId(info.userId())
        .build();
  }

}
