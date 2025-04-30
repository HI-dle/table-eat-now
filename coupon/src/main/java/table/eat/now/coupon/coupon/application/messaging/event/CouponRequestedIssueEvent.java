package table.eat.now.coupon.coupon.application.messaging.event;

import java.time.LocalDateTime;
import lombok.Builder;
import table.eat.now.common.resolver.dto.CurrentUserInfoDto;
import table.eat.now.coupon.coupon.domain.entity.Coupon;
import table.eat.now.coupon.user_coupon.application.dto.request.IssueUserCouponCommand;

@Builder
public record CouponRequestedIssueEvent(
    String eventType,
    String userCouponUuid,
    CouponRequestedIssuePayload payload
) implements CouponEvent {

  public static CouponRequestedIssueEvent of(String userCouponUuid, CurrentUserInfoDto userInfoDto, Coupon coupon) {
    return CouponRequestedIssueEvent.builder()
        .eventType(EventType.COUPON_REQUESTED_ISSUE.name())
        .userCouponUuid(userCouponUuid)
        .payload(CouponRequestedIssuePayload.builder()
            .userCouponUuid(userCouponUuid)
            .userId(userInfoDto.userId())
            .couponUuid(coupon.getCouponUuid())
            .name(coupon.getName())
            .expiresAt(coupon.calcExpireAt())
            .build())
        .build();
  }

  public static CouponRequestedIssueEvent of(String userCouponUuid, Long userId, Coupon coupon) {
    return CouponRequestedIssueEvent.builder()
        .eventType(EventType.COUPON_REQUESTED_ISSUE.name())
        .userCouponUuid(userCouponUuid)
        .payload(CouponRequestedIssuePayload.builder()
            .userCouponUuid(userCouponUuid)
            .userId(userId)
            .couponUuid(coupon.getCouponUuid())
            .name(coupon.getName())
            .expiresAt(coupon.calcExpireAt())
            .build())
        .build();
  }

  public IssueUserCouponCommand toCommand() {
    return IssueUserCouponCommand.builder()
        .userCouponUuid(userCouponUuid)
        .userId(payload.userId())
        .couponUuid(payload.couponUuid())
        .name(payload.name())
        .expiresAt(payload.expiresAt())
        .build();
  }

  @Builder
  public record CouponRequestedIssuePayload(
      String userCouponUuid,
      Long userId,
      String couponUuid,
      String name,
      LocalDateTime expiresAt
  ) {

  }
}
