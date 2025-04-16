package table.eat.now.promotion.promotion.infrastructure.kafka.dto;

import table.eat.now.promotion.promotion.domain.entity.repository.event.PromotionParticipantDto;


public record PromotionUserSavePayloadQuery(
    Long userId,
    String promotionUuid
) {
  public static PromotionParticipantDto from(PromotionUserSavePayloadQuery query) {
    return PromotionParticipantDto.builder()
        .userId(query.userId())
        .promotionUuid(query.promotionUuid())
        .build();
  }
}
