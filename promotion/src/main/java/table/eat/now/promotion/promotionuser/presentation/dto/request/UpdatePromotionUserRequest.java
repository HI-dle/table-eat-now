package table.eat.now.promotion.promotionuser.presentation.dto.request;

import jakarta.validation.constraints.NotNull;
import table.eat.now.promotion.promotionuser.application.dto.request.UpdatePromotionUserCommand;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 08.
 */
public record UpdatePromotionUserRequest(@NotNull
                                         Long userId,
                                         @NotNull
                                         String promotionUuid) {

  public UpdatePromotionUserCommand toApplication() {
    return new UpdatePromotionUserCommand(userId, promotionUuid);
  }
}
