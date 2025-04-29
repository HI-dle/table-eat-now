package table.eat.now.coupon.coupon.application.usecase.dto.request;

import lombok.Builder;
import table.eat.now.coupon.coupon.domain.command.CouponIssuance;
import table.eat.now.coupon.coupon.domain.info.CouponProfile;

@Builder
public record IssuePromotionCouponCommand(
    String couponUuid,
    Long userId,
    Long timestamp
) {
  public CouponIssuance toDomain(CouponProfile profile) {
    return CouponIssuance.builder()
        .couponProfile(profile)
        .couponUuid(couponUuid)
        .userId(userId)
        .timestamp(timestamp)
        .build();
  }
}
