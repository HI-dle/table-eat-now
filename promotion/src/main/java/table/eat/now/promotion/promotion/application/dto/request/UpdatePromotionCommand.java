package table.eat.now.promotion.promotion.application.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 10.
 */
public record UpdatePromotionCommand(String promotionName,
                                     String description,
                                     LocalDateTime startTime,
                                     LocalDateTime endTime,
                                     BigDecimal discountAmount,
                                     String promotionStatus,
                                     String promotionType) {

}
