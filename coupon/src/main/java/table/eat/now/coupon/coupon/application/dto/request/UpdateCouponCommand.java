package table.eat.now.coupon.coupon.application.dto.request;

import java.time.LocalDateTime;
import lombok.Builder;
import table.eat.now.coupon.coupon.domain.command.UpdateCoupon;
import table.eat.now.coupon.coupon.domain.entity.CouponType;

@Builder
public record UpdateCouponCommand(
  String name,
  String type,
  LocalDateTime startAt,
  LocalDateTime endAt,
  Integer count,
  Boolean allowDuplicate,
  Integer minPurchaseAmount,
  Integer amount,
  Integer percent,
  Integer maxDiscountAmount
) {

  public UpdateCoupon toDomainCommand() {
    return UpdateCoupon.builder()
        .name(name)
        .type(CouponType.valueOf(type))
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
}
