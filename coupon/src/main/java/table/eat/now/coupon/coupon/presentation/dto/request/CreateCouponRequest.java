package table.eat.now.coupon.coupon.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDateTime;
import lombok.Builder;
import table.eat.now.coupon.coupon.application.dto.request.CreateCouponCommand;

@Builder
public record CreateCouponRequest(
    @NotBlank String name,
    @NotNull CouponType type,
    @NotNull LocalDateTime startAt,
    @NotNull LocalDateTime endAt,
    @Positive Integer count,
    @NotNull Boolean allowDuplicate,
    @NotNull Integer minPurchaseAmount,
    Integer amount,
    Integer percent,
    Integer maxDiscountAmount
) {

  public CreateCouponCommand toCommand() {
    return CreateCouponCommand.builder()
        .name(name)
        .type(type.toString())
        .startAt(startAt)
        .endAt(endAt)
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
