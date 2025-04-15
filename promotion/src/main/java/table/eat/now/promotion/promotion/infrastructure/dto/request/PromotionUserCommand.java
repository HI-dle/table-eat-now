package table.eat.now.promotion.promotion.infrastructure.dto.request;

import lombok.Builder;
import table.eat.now.promotion.promotion.domain.entity.repository.event.PromotionParticipant;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 15.
 */
@Builder
public record PromotionUserCommand(Long userId,
                                   String promotionUuid,
                                   String promotionName) {

  public static PromotionUserCommand from(PromotionParticipant participant) {
    return PromotionUserCommand.builder()
        .userId(participant.userId())
        .promotionUuid(participant.promotionUuid())
        .promotionName(participant.promotionName())
        .build();
  }

}
