package table.eat.now.promotion.promotion.domain.entity.repository.search;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 09.
 */
public record PromotionSearchCriteria(String promotionName,
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

}
