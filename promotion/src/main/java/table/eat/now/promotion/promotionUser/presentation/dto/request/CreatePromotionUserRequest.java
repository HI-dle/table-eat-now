package table.eat.now.promotion.promotionUser.presentation.dto.request;

import jakarta.validation.constraints.NotNull;
import table.eat.now.promotion.promotionUser.application.dto.request.CreatePromotionUserCommand;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 08.
 */
public record CreatePromotionUserRequest(@NotNull
                                         Long userId) {

  public CreatePromotionUserCommand toApplication() {
    return new CreatePromotionUserCommand(userId);
  }
}
