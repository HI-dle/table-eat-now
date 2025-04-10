package table.eat.now.promotion.promotion.domain.entity.repository.search;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Builder;
import table.eat.now.promotion.promotion.domain.entity.Promotion;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 09.
 */
@Builder
public record PromotionSearchCriteriaQuery(Long promotionId,
                                           String promotionUuid,
                                           String promotionName,
                                           String description,
                                           LocalDateTime startTime,
                                           LocalDateTime endTime,
                                           BigDecimal discountAmount,
                                           String promotionStatus,
                                           String promotionType) {

  public static PromotionSearchCriteriaQuery from(Promotion promotion) {
    return PromotionSearchCriteriaQuery.builder()
        .promotionId(promotion.getId())
        .promotionUuid(promotion.getPromotionUuid())
        .promotionName(promotion.getDetails().getPromotionName())
        .description(promotion.getDetails().getDescription())
        .startTime(promotion.getPeriod().getStartTime())
        .endTime(promotion.getPeriod().getEndTime())
        .discountAmount(promotion.getDiscountPrice().getDiscountAmount())
        .promotionStatus(promotion.getPromotionStatus().name())
        .promotionType(promotion.getPromotionType().name())
        .build();
  }

}
