package table.eat.now.promotion.promotionuser.infrastructure.kafka.dto;

import lombok.Builder;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 28.
 */
@Builder
public record PromotionSendPayload(String promotionUuid,
                                   Long userId) {

}
