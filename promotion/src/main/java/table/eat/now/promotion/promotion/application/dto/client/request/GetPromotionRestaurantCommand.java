package table.eat.now.promotion.promotion.application.dto.client.request;

import java.util.Set;
import lombok.Builder;
import table.eat.now.promotion.promotion.application.dto.request.GetPromotionsFeignCommand;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 13.
 */
@Builder
public record GetPromotionRestaurantCommand(Set<String> promotionUuid) {

  public static GetPromotionRestaurantCommand from(GetPromotionsFeignCommand command) {
    return GetPromotionRestaurantCommand.builder()
        .promotionUuid(command.promotionUuids())
        .build();
  }
}
