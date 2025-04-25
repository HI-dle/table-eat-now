package table.eat.now.coupon.user_coupon.infrastructure.client.feign.dto.response;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import lombok.Builder;
import table.eat.now.coupon.user_coupon.application.client.dto.response.GetCouponInfoI;

@Builder
public record GetCouponsResponseI(
    Map<String, GetCouponResponseI> coupons
) {

  public Map<String, GetCouponInfoI> toInfo() {
    return coupons.entrySet()
        .stream()
        .collect(Collectors.toMap(
            Entry::getKey,
            e -> e.getValue().toInfo()
        ));
  }
  @Builder
  public record GetCouponResponseI(
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
      Long createdBy
  ) {

    public GetCouponInfoI toInfo() {
      return GetCouponInfoI.builder()
          .couponId(couponId)
          .couponUuid(couponUuid)
          .name(name)
          .type(type)
          .label(label)
          .issueStartAt(issueStartAt)
          .issueEndAt(issueEndAt)
          .expireAt(expireAt)
          .validDays(validDays)
          .count(count)
          .allowDuplicate(allowDuplicate)
          .minPurchaseAmount(minPurchaseAmount)
          .amount(amount)
          .percent(percent)
          .maxDiscountAmount(maxDiscountAmount)
          .createdAt(createdAt)
          .createdBy(createdBy)
          .build();
    }
  }
}
