package table.eat.now.promotion.promotion.application.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Builder;
import table.eat.now.promotion.promotion.domain.entity.Promotion;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 08.
 */
@Builder
public record CreatePromotionInfo(Long promotionId,
                                  String promotionUuid,
                                  String promotionName,
                                  String description,
                                  LocalDateTime startTime,
                                  LocalDateTime endTime,
                                  BigDecimal discountAmount,
                                  String promotionStatus,
                                  String promotionType,
                                  Integer maxParticipant) {

  public static CreatePromotionInfo from(Promotion promotion) {
    return CreatePromotionInfo.builder()
        .promotionId(promotion.getId())
        .promotionUuid(promotion.getPromotionUuid())
        .promotionName(promotion.getDetails().getPromotionName())
        .description(promotion.getDetails().getDescription())
        .startTime(promotion.getPeriod().getStartTime())
        .endTime(promotion.getPeriod().getEndTime())
        .discountAmount(promotion.getDiscountPrice().getDiscountAmount())
        .promotionStatus(promotion.getPromotionStatus().description())
        .promotionType(promotion.getPromotionType().description())
        .maxParticipant(promotion.getMaxParticipant().getMaxParticipantsValue())
        .build();
  }
}
