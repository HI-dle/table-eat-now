package table.eat.now.promotion.promotionuser.application.event.dto;

import lombok.Builder;

@Builder
public record PromotionUserSavePayloadInfo(
    Long userId,
    String promotionUuid
) {
}
