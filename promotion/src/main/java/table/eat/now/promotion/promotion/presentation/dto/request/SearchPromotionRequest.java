package table.eat.now.promotion.promotion.presentation.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;
import table.eat.now.promotion.promotion.application.dto.request.SearchPromotionCommand;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 08.
 */
public record SearchPromotionRequest(@Size(max = 500)
                                     String promotionName,
                                     String description,
                                     @FutureOrPresent(message = "시작 시간은 현재 이후여야 합니다.")
                                     @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                                     LocalDateTime startTime,
                                     @Future(message = "현재 시간보다 이후여야 합니다.")
                                     @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                                     LocalDateTime endTime,
                                     @DecimalMin(value = "0.0", inclusive = false,
                                         message = "할인 금액은 0 이상이어야 합니다.")
                                     BigDecimal discountAmount,
                                     @Pattern(regexp = "READY|RUNNING|CLOSED",
                                         message = "유효하지 않은 프로모션 상태입니다.")
                                     String promotionStatus,
                                     @Pattern(regexp = "COUPON|RESTAURANT",
                                         message = "유효하지 않은 프로모션 타입입니다.")
                                     String promotionType,
                                     Boolean isAsc,
                                     String sortBy,
                                     int page,
                                     int size

) {

  public SearchPromotionCommand toApplication() {
    return new SearchPromotionCommand(
        promotionName,
        description,
        startTime,
        endTime,
        discountAmount,
        promotionStatus,
        promotionType,
        isAsc,
        sortBy,
        page,
        size
    );
  }
}
