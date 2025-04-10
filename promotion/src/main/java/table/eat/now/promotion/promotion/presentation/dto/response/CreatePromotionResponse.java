package table.eat.now.promotion.promotion.presentation.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Builder;
import table.eat.now.promotion.promotion.application.dto.response.CreatePromotionInfo;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 08.
 */
@Builder
public record CreatePromotionResponse(Long promotionId,
                                      String promotionUuid,
                                      String promotionName,
                                      String description,
                                      LocalDateTime startTime,
                                      LocalDateTime endTime,
                                      BigDecimal discountAmount,
                                      String promotionStatus,
                                      String promotionType) {

  public static CreatePromotionResponse from(CreatePromotionInfo createPromotionInfo) {
    return CreatePromotionResponse.builder()
        .promotionId(createPromotionInfo.promotionId())
        .promotionUuid(createPromotionInfo.promotionUuid())
        .promotionName(createPromotionInfo.promotionName())
        .description(createPromotionInfo.description())
        .startTime(createPromotionInfo.startTime())
        .endTime(createPromotionInfo.endTime())
        .discountAmount(createPromotionInfo.discountAmount())
        .promotionStatus(createPromotionInfo.promotionStatus())
        .promotionType(createPromotionInfo.promotionType())
        .build();
  }

}
