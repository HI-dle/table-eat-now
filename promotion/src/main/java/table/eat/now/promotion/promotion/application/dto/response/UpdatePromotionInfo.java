package table.eat.now.promotion.promotion.application.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Builder;
import table.eat.now.promotion.promotion.domain.entity.Promotion;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 10.
 */
@Builder
public record UpdatePromotionInfo(Long promotionId,
                                  String couponUuid,
                                  String promotionUuid,
                                  String promotionName,
                                  String description,
                                  LocalDateTime startTime,
                                  LocalDateTime endTime,
                                  BigDecimal discountAmount,
                                  String promotionStatus,
                                  String promotionType,
                                  Integer maxParticipant) {

  public static UpdatePromotionInfo from(Promotion promotion) {
    return UpdatePromotionInfo.builder()
        .promotionId(promotion.getId())
        .couponUuid(promotion.getCouponUuid())
        .promotionUuid(promotion.getPromotionUuid())
        .promotionName(promotion.getDetails().getPromotionName())
        .description(promotion.getDetails().getDescription())
        .startTime(promotion.getPeriod().getStartTime())
        .endTime(promotion.getPeriod().getEndTime())
        .discountAmount(promotion.getDiscountPrice().getDiscountAmount())
        .promotionStatus(promotion.getPromotionStatus().toString())
        .promotionType(promotion.getPromotionType().toString())
        .maxParticipant(promotion.getMaxParticipant().getMaxParticipantsValue())
        .build();
  }
}
