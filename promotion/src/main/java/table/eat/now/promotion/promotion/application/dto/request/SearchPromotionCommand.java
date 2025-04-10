package table.eat.now.promotion.promotion.application.dto.request;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import table.eat.now.promotion.promotion.domain.entity.Promotion;
import table.eat.now.promotion.promotion.domain.entity.PromotionStatus;
import table.eat.now.promotion.promotion.domain.entity.PromotionType;
import table.eat.now.promotion.promotion.domain.entity.repository.search.PromotionSearchCriteria;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 08.
 */
public record SearchPromotionCommand(String promotionName,
                                     String description,
                                     LocalDateTime startTime,
                                     LocalDateTime endTime,
                                     BigDecimal discountAmount,
                                     String promotionStatus,
                                     String promotionType,
                                     Boolean isAsc,
                                     String sortBy,
                                     int page,
                                     int size) {

  public PromotionSearchCriteria toCriteria() {
    return new PromotionSearchCriteria(
        promotionName, description, startTime, endTime,
        discountAmount,promotionStatus, promotionType,
        isAsc, sortBy, page, size
    );
  }

}
