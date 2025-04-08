package table.eat.now.coupon.coupon.presentation.dto.response;

import java.util.UUID;
import lombok.Builder;

@Builder
public record UpdateCouponResponse(
    UUID couponUuid
) {

  public static UpdateCouponResponse of(UUID couponUuid) {
    return UpdateCouponResponse.builder().couponUuid(couponUuid).build();
  }
}
