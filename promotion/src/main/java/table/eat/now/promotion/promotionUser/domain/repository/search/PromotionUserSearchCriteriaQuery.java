package table.eat.now.promotion.promotionUser.domain.repository.search;

import lombok.Builder;
import table.eat.now.promotion.promotionUser.domain.entity.PromotionUser;


/**
 * @author : hanjihoon
 * @Date : 2025. 04. 09.
 */
@Builder
public record PromotionUserSearchCriteriaQuery(String promotionUserUuid,
                                               String promotionUuid,
                                               Long userId) {

  public static PromotionUserSearchCriteriaQuery from(PromotionUser promotionUser) {
    return PromotionUserSearchCriteriaQuery.builder()
        .promotionUserUuid(promotionUser.getPromotionUserUuid())
        .promotionUuid(promotionUser.getPromotionUuid())
        .userId(promotionUser.getUserId())
        .build();
  }

}
