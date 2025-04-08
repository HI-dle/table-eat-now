package table.eat.now.promotion.application.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;
import table.eat.now.promotion.domain.entity.Promotion;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 08.
 */
@Builder
public record CreatePromotionInfo(Long promotionId,
                                  UUID promotionUuid,
                                  String promotionName,
                                  String description,
                                  LocalDateTime startTime,
                                  LocalDateTime endTime,
                                  BigDecimal discountAmount,
                                  String promotionStatus,
                                  String promotionType) {

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
        .build();
  }
}
