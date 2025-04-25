package table.eat.now.coupon.coupon.application.dto.request;

import java.time.LocalDateTime;
import lombok.Builder;
import table.eat.now.coupon.coupon.domain.command.UpdateCoupon;
import table.eat.now.coupon.coupon.domain.entity.CouponLabel;
import table.eat.now.coupon.coupon.domain.entity.CouponType;

@Builder
public record UpdateCouponCommand(
  String name,
  String type,
  String label,
  LocalDateTime issueStartAt,
  LocalDateTime issueEndAt,
  LocalDateTime expireAt,
  Integer validDays,
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
        .type(CouponType.parse(type))
        .label(CouponLabel.parse(label))
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
