package table.eat.now.promotion.promotion.presentation.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Builder;
import table.eat.now.promotion.promotion.application.dto.response.SearchPromotionInfo;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 08.
 */
@Builder
public record SearchPromotionResponse(Long promotionId,
                                      String promotionUuid,
                                      String promotionName,
                                      String description,
                                      LocalDateTime startTime,
                                      LocalDateTime endTime,
                                      BigDecimal discountAmount,
                                      String promotionStatus,
                                      String promotionType) {

  public static SearchPromotionResponse from(SearchPromotionInfo searchPromotionInfo) {
    return SearchPromotionResponse.builder()
        .promotionId(searchPromotionInfo.promotionId())
        .promotionUuid(searchPromotionInfo.promotionUuid())
        .promotionName(searchPromotionInfo.promotionName())
        .description(searchPromotionInfo.description())
        .startTime(searchPromotionInfo.startTime())
        .endTime(searchPromotionInfo.endTime())
        .discountAmount(searchPromotionInfo.discountAmount())
        .promotionStatus(searchPromotionInfo.promotionStatus())
        .promotionType(searchPromotionInfo.promotionType())
        .build();
  }

}
