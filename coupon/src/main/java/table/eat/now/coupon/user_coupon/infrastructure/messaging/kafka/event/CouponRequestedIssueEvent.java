package table.eat.now.coupon.user_coupon.infrastructure.messaging.kafka.event;

import java.time.LocalDateTime;
import lombok.Builder;
import table.eat.now.coupon.user_coupon.application.dto.request.IssueUserCouponCommand;

@Builder
public record CouponRequestedIssueEvent(
    String eventType,
    String userCouponUuid,
    CouponRequestedIssuePayload payload
) implements CouponEvent {

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
  record CouponRequestedIssuePayload(
      String userCouponUuid,
      Long userId,
      String couponUuid,
      String name,
      LocalDateTime expiresAt
  ) {

  }
}
