package table.eat.now.coupon.coupon.infrastructure.messaging.kafka.dto;

import table.eat.now.common.resolver.dto.CurrentUserInfoDto;
import table.eat.now.coupon.coupon.application.usecase.dto.request.IssuePromotionCouponCommand;

public record PromotionParticipatedCouponEvent(
    String eventType,
    PromotionParticipatedPayload payload,
    CurrentUserInfoDto userInfo,
    String couponUuid
) implements PromotionEvent {

  public IssuePromotionCouponCommand toCommand(Long timestamp) {
    return IssuePromotionCouponCommand.builder()
        .couponUuid(couponUuid)
        .userId(payload.userId())
        .timestamp(timestamp)
        .build();
  }
}
