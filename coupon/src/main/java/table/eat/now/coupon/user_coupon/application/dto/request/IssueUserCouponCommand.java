package table.eat.now.coupon.user_coupon.application.dto.request;

import java.time.LocalDateTime;
import lombok.Builder;
import table.eat.now.coupon.user_coupon.domain.entity.UserCoupon;

@Builder
public record IssueUserCouponCommand(
    String userCouponUuid,
    String couponUuid,
    Long userId,
    String name,
    LocalDateTime expiresAt
) {

  public UserCoupon toEntity() {
    return UserCoupon.of(userCouponUuid, couponUuid, userId, name, expiresAt);
  }
}
