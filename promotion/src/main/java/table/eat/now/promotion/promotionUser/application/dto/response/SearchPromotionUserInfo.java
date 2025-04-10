package table.eat.now.promotion.promotionUser.application.dto.response;

import lombok.Builder;
import table.eat.now.promotion.promotionUser.domain.repository.search.PromotionUserSearchCriteriaQuery;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 08.
 */
@Builder
public record SearchPromotionUserInfo(String promotionUserUuid,
                                      String promotionUuid,
                                      Long userId) {

  public static SearchPromotionUserInfo from(PromotionUserSearchCriteriaQuery query) {
    return SearchPromotionUserInfo.builder()
        .promotionUserUuid(query.promotionUserUuid())
        .promotionUuid(query.promotionUuid())
        .userId(query.userId())
        .build();
  }
}
