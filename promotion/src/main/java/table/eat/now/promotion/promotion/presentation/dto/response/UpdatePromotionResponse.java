package table.eat.now.promotion.promotion.presentation.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;
import table.eat.now.promotion.promotion.application.dto.response.UpdatePromotionInfo;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 10.
 */
@Builder
public record UpdatePromotionResponse(Long promotionId,
                                      String promotionUuid,
                                      String promotionName,
                                      String description,
                                      LocalDateTime startTime,
                                      LocalDateTime endTime,
                                      BigDecimal discountAmount,
                                      String promotionStatus,
                                      String promotionType) {

  public static UpdatePromotionResponse from(UpdatePromotionInfo info) {
    return UpdatePromotionResponse.builder()
        .promotionId(info.promotionId())
        .promotionUuid(info.promotionUuid())
        .promotionName(info.promotionName())
        .description(info.description())
        .startTime(info.startTime())
        .endTime(info.endTime())
        .discountAmount(info.discountAmount())
        .promotionStatus(info.promotionStatus())
        .promotionType(info.promotionType())
        .build();
  }
}
