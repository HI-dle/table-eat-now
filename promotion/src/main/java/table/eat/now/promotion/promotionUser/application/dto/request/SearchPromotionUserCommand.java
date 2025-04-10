package table.eat.now.promotion.promotionUser.application.dto.request;

import table.eat.now.promotion.promotionUser.domain.repository.search.PromotionUserSearchCriteria;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 08.
 */
public record SearchPromotionUserCommand(Long userId,
                                         String promotionUuid,
                                         Boolean isAsc,
                                         String sortBy,
                                         int page,
                                         int size) {

  public PromotionUserSearchCriteria toCriteria() {
    return new PromotionUserSearchCriteria(
        userId,
        promotionUuid,
        isAsc,
        sortBy,
        page,
        size
    );
  }
}
