package table.eat.now.promotion.promotion.application.dto.request;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import table.eat.now.promotion.promotion.domain.entity.Promotion;
import table.eat.now.promotion.promotion.domain.entity.PromotionStatus;
import table.eat.now.promotion.promotion.domain.entity.PromotionType;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 08.
 */
public record CreatePromotionCommand(String couponUuid,
                                     String promotionName,
                                     String description,
                                     LocalDateTime startTime,
                                     LocalDateTime endTime,
                                     BigDecimal discountAmount,
                                     String promotionStatus,
                                     String promotionType,
                                     Integer maxParticipant) {

  public Promotion toEntity() {
    return Promotion.of(
        couponUuid, promotionName, description, startTime, endTime,
        discountAmount, PromotionStatus.valueOf(promotionStatus),
        PromotionType.valueOf(promotionType), maxParticipant);
  }

}
