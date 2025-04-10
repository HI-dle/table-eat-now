package table.eat.now.coupon.coupon.application.dto.event;

import java.time.LocalDateTime;
import lombok.Builder;
import table.eat.now.common.resolver.dto.CurrentUserInfoDto;
import table.eat.now.coupon.coupon.domain.entity.Coupon;

@Builder
public record IssueUserCouponEvent(
    Long userId,
    String couponUuid,
    String userCouponUuid,
    String name,
    LocalDateTime expiresAt
) {

  public static IssueUserCouponEvent from(String userCouponUuid, CurrentUserInfoDto userInfoDto, Coupon coupon) {
    return IssueUserCouponEvent.builder()
        .userId(userInfoDto.userId())
        .couponUuid(coupon.getCouponUuid())
        .userCouponUuid(userCouponUuid)
        .name(coupon.getName())
        .expiresAt(coupon.getPeriod().getEndAt())
        .build();
  }
}
