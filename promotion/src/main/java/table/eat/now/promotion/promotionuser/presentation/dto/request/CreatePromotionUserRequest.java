package table.eat.now.promotion.promotionuser.presentation.dto.request;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import table.eat.now.promotion.promotionuser.application.dto.request.CreatePromotionUserCommand;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 08.
 */
public record CreatePromotionUserRequest(@NotNull
                                         Long userId,
                                         @NotNull
                                         UUID promotionUuid) {

  public CreatePromotionUserCommand toApplication() {
    return new CreatePromotionUserCommand(userId, promotionUuid.toString());
  }
}
