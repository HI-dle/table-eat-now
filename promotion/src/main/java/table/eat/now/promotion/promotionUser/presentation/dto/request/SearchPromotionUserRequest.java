package table.eat.now.promotion.promotionUser.presentation.dto.request;

import jakarta.validation.constraints.NotNull;
import table.eat.now.promotion.promotionUser.application.dto.request.CreatePromotionUserCommand;
import table.eat.now.promotion.promotionUser.application.dto.request.SearchPromotionUserCommand;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 08.
 */
public record SearchPromotionUserRequest(Long userId,
                                         String promotionUuid,
                                         Boolean isAsc,
                                         String sortBy,
                                         int page,
                                         int size) {

  public SearchPromotionUserCommand toApplication() {
    return new SearchPromotionUserCommand(
        userId,
        promotionUuid,
        isAsc,
        sortBy,
        page,
        size
    );
  }
}
