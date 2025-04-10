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
                                          String promotionUuid,
                                          Long userId) {

  public static SearchPromotionUserResponse from(SearchPromotionUserInfo info) {
    return SearchPromotionUserResponse.builder()
        .promotionUserUuid(info.promotionUserUuid())
        .promotionUuid(info.promotionUuid())
        .userId(info.userId())
        .build();
  }

}
