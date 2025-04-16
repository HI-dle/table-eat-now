package table.eat.now.promotion.promotion.application.event.produce;

import lombok.Builder;
import table.eat.now.promotion.promotion.domain.entity.repository.event.PromotionParticipantDto;

@Builder
public record PromotionUserSavePayload(
    Long userId,
    String promotionUuid
) {
  public static PromotionUserSavePayload from(PromotionParticipantDto dto) {
    return PromotionUserSavePayload.builder()
        .userId(dto.userId())
        .promotionUuid(dto.promotionUuid())
        .build();
  }
}
