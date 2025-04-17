package table.eat.now.promotion.promotion.domain.entity.repository.event;

import lombok.Builder;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 15.
 */
@Builder
public record PromotionParticipantDto(Long userId,
                                      String promotionUuid) {

}
