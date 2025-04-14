package table.eat.now.promotion.promotionuser.presentation.dto.request;

import java.util.UUID;
import table.eat.now.promotion.promotionuser.application.dto.request.SearchPromotionUserCommand;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 08.
 */
public record SearchPromotionUserRequest(Long userId,
                                         UUID promotionUuid,
                                         Boolean isAsc,
                                         String sortBy,
                                         int page,
                                         int size) {

  public SearchPromotionUserCommand toApplication() {
    return new SearchPromotionUserCommand(
        userId,
        promotionUuid.toString(),
        isAsc,
        sortBy,
        page,
        size
    );
  }
}
