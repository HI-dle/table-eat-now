package table.eat.now.promotion.promotion.presentation.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import table.eat.now.promotion.promotion.application.dto.request.CreatePromotionCommand;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 08.
 */
public record CreatePromotionRequest(@NotBlank
                                     @Size(max = 500)
                                     String promotionName,
                                     @NotBlank
                                     String description,
                                     @NotNull
                                     @FutureOrPresent(message = "시작 시간은 현재 이후여야 합니다.")
                                     LocalDateTime startTime,
                                     @NotNull
                                     @Future(message = "현재 시간보다 이후여야 합니다.")
                                     LocalDateTime endTime,
                                     @NotNull
                                     @DecimalMin(value = "0.0", inclusive = false, message = "할인 금액은 0 이상이어야 합니다.")
                                     BigDecimal discountAmount,
                                     @NotBlank(message = "프로모션 상태는 필수입니다.")
                                     @Pattern(regexp = "READY|RUNNING|CLOSED", message = "유효하지 않은 프로모션 상태입니다.")
                                     String promotionStatus,
                                     @NotBlank(message = "프로모션 타입은 필수입니다.")
                                     @Pattern(regexp = "COUPON|RESTAURANT", message = "유효하지 않은 프로모션 타입입니다.")
                                     String promotionType

) {

  public CreatePromotionCommand toApplication() {
    return new CreatePromotionCommand(
        promotionName,
        description,
        startTime,
        endTime,
        discountAmount,
        promotionStatus,
        promotionType
    );
  }
}
