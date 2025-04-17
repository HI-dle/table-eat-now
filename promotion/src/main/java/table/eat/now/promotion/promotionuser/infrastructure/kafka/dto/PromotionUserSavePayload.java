package table.eat.now.promotion.promotionuser.infrastructure.kafka.dto;

import lombok.Builder;
import table.eat.now.promotion.promotionuser.application.event.dto.PromotionUserSavePayloadInfo;

@Builder
public record PromotionUserSavePayload(
    Long userId,
    String promotionUuid
) {
  public static PromotionUserSavePayloadInfo from(PromotionUserSavePayload promotionUserSavePayload) {
    return PromotionUserSavePayloadInfo.builder()
        .userId(promotionUserSavePayload.userId())
        .promotionUuid(promotionUserSavePayload.promotionUuid())
        .build();
  }
}
