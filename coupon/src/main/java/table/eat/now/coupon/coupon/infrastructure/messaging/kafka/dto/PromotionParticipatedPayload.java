package table.eat.now.coupon.coupon.infrastructure.messaging.kafka.dto;

import lombok.Builder;

@Builder
public record PromotionParticipatedPayload(
    Long userId,
    String promotionUuid
) {

}
