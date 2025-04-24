package table.eat.now.coupon.user_coupon.infrastructure.messaging.kafka.dto;

import java.time.LocalDate;
import java.util.UUID;
import table.eat.now.common.resolver.dto.CurrentUserInfoDto;
import table.eat.now.coupon.user_coupon.application.dto.request.IssueUserCouponCommand;

public record PromotionParticipatedCouponEvent(
    EventType eventType,
    PromotionParticipatedPayload payload,
    CurrentUserInfoDto userInfo,
    String couponUuid
) implements PromotionEvent {

  public IssueUserCouponCommand toCommand() {
    return IssueUserCouponCommand.builder()
        .couponUuid(couponUuid)
        .name("쿠폰 인포 필요")
        .userId(payload.userId())
        .userCouponUuid(UUID.randomUUID().toString())
        .expiresAt(LocalDate.now().plusDays(7 + 1).atStartOfDay()) // todo 수정
        .build();
  }
}
