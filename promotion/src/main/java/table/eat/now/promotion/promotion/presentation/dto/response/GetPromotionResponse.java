package table.eat.now.promotion.promotion.presentation.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Builder;
import table.eat.now.promotion.promotion.application.dto.response.GetPromotionInfo;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 08.
 */
@Builder
public record GetPromotionResponse(Long promotionId,
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

  public static GetPromotionResponse from(GetPromotionInfo getPromotionInfo) {
    return GetPromotionResponse.builder()
        .promotionId(getPromotionInfo.promotionId())
        .couponUuid(getPromotionInfo.couponUuid())
        .promotionUuid(getPromotionInfo.promotionUuid())
        .promotionName(getPromotionInfo.promotionName())
        .description(getPromotionInfo.description())
        .startTime(getPromotionInfo.startTime())
        .endTime(getPromotionInfo.endTime())
        .discountAmount(getPromotionInfo.discountAmount())
        .promotionStatus(getPromotionInfo.promotionStatus())
        .promotionType(getPromotionInfo.promotionType())
        .maxParticipant(getPromotionInfo.maxParticipant())
        .build();
  }

}
