package table.eat.now.promotion.promotion.application.event.produce;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 25.
 */
public record PromotionSchedulePayload(Long promotionId,
                                       String promotionUuid,
                                       String promotionName,
                                       String description,
                                       LocalDateTime startTime,
                                       LocalDateTime endTime,
                                       BigDecimal discountAmount,
                                       String promotionStatus,
                                       String promotionType) {

}
