package table.eat.now.coupon.user_coupon.application.client.dto.response;

import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record GetCouponInfoI(
    Long couponId,
    String couponUuid,
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
    Integer maxDiscountAmount,
    LocalDateTime createdAt,
    Long createdBy,
    Long version
) {
}
