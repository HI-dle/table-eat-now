package table.eat.now.coupon.coupon.presentation.dto.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import lombok.Builder;
import table.eat.now.coupon.coupon.application.dto.request.UpdateCouponCommand;

@Builder
public record UpdateCouponRequest(
    @NotBlank @Size(max = 200) String name,
    @NotBlank @Pattern(regexp = "(?i)^(FIXED_DISCOUNT|PERCENT_DISCOUNT)$") String type,
    @NotBlank @Pattern(regexp = "(?i)^(GENERAL|PROMOTION|HOT)$") String label,
    @NotNull @Future LocalDateTime issueStartAt,
    @NotNull @Future LocalDateTime issueEndAt,
    @Future LocalDateTime expireAt,
    @Positive Integer validDays,
    @NotNull @PositiveOrZero Integer count,
    @NotNull Boolean allowDuplicate,
    @NotNull @Positive Integer minPurchaseAmount,
    Integer amount,
    Integer percent,
    Integer maxDiscountAmount
) {

  public UpdateCouponCommand toCommand() {
    return UpdateCouponCommand.builder()
        .name(name)
        .type(type)
        .label(label)
        .issueStartAt(issueStartAt)
        .issueEndAt(issueEndAt)
        .expireAt(expireAt)
        .validDays(validDays)
        .count(count)
        .allowDuplicate(allowDuplicate)
        .minPurchaseAmount(minPurchaseAmount)
        .amount(amount)
        .percent(percent)
        .maxDiscountAmount(maxDiscountAmount)
        .build();
  }
}
