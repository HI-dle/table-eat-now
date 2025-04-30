package table.eat.now.coupon.coupon.domain.command;

import lombok.Builder;
import table.eat.now.coupon.coupon.domain.info.CouponProfile;

@Builder
public record CouponIssuance(
    CouponProfile couponProfile,
    String couponUuid,
    Long userId,
    Long timestamp
) {

}
