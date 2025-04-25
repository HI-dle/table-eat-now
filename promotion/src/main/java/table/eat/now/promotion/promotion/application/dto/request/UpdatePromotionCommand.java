package table.eat.now.promotion.promotion.application.dto.request;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 10.
 */
public record UpdatePromotionCommand(String couponUuid,
                                     String promotionName,
                                     String description,
                                     LocalDateTime startTime,
                                     LocalDateTime endTime,
                                     BigDecimal discountAmount,
                                     String promotionStatus,
                                     String promotionType,
                                     Integer maxParticipant) {

}
