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
import table.eat.now.coupon.coupon.application.dto.request.CreateCouponCommand;

@Builder
public record CreateCouponRequest(
    @NotBlank @Size(max = 200) String name,
    @NotNull CouponType type,
    @NotBlank @Pattern(regexp = "(?i)^(GENERAL|PROMOTION|HOT)$") String label,
    @NotNull @Future LocalDateTime startAt,
    @NotNull @Future LocalDateTime endAt,
    @Positive Integer validDays,
    @NotNull @PositiveOrZero Integer count,
    @NotNull Boolean allowDuplicate,
    @NotNull @Positive Integer minPurchaseAmount,
    Integer amount,
    Integer percent,
    Integer maxDiscountAmount
) {

  public CreateCouponCommand toCommand() {
    return CreateCouponCommand.builder()
        .name(name)
        .type(type.toString())
        .label(label)
        .startAt(startAt)
        .endAt(endAt)
        .validDays(validDays)
        .count(count)
        .allowDuplicate(allowDuplicate)
        .minPurchaseAmount(minPurchaseAmount)
        .amount(amount)
        .percent(percent)
        .maxDiscountAmount(maxDiscountAmount)
        .build();
  }

  public enum CouponType {
    FIXED_DISCOUNT, PERCENT_DISCOUNT
  }
}
