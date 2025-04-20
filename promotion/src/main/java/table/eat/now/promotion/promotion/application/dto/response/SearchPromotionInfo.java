package table.eat.now.promotion.promotion.application.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Builder;
import table.eat.now.promotion.promotion.domain.entity.repository.search.PromotionSearchCriteriaQuery;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 08.
 */
@Builder
public record SearchPromotionInfo(Long promotionId,
                                  String couponUuid,
                                  String promotionUuid,
                                  String promotionName,
                                  String description,
                                  LocalDateTime startTime,
                                  LocalDateTime endTime,
                                  BigDecimal discountAmount,
                                  String promotionStatus,
                                  String promotionType) {

  public static SearchPromotionInfo from(PromotionSearchCriteriaQuery query) {
    return SearchPromotionInfo.builder()
        .promotionId(query.promotionId())
        .couponUuid(query.couponUuid())
        .promotionUuid(query.promotionUuid())
        .promotionName(query.promotionName())
        .description(query.description())
        .startTime(query.startTime())
        .endTime(query.endTime())
        .discountAmount(query.discountAmount())
        .promotionStatus(query.promotionStatus())
        .promotionType(query.promotionType())
        .build();
  }
}
