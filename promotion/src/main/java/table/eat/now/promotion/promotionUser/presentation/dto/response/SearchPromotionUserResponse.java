package table.eat.now.promotion.promotionUser.presentation.dto.response;

import lombok.Builder;
import table.eat.now.promotion.promotionUser.application.dto.response.CreatePromotionUserInfo;
import table.eat.now.promotion.promotionUser.application.dto.response.SearchPromotionUserInfo;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 08.
 */
@Builder
public record SearchPromotionUserResponse(String promotionUserUuid,
                                          Long userId,
                                          String promotionUuid) {

  public static SearchPromotionUserResponse from(SearchPromotionUserInfo info) {
    return SearchPromotionUserResponse.builder()
        .promotionUserUuid(info.promotionUserUuid())
        .userId(info.userId())
        .promotionUuid(info.promotionUuid())
        .build();
  }

}
