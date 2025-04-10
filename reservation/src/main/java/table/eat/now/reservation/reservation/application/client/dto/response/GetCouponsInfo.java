/**
 * @author : jieun(je-pa)
 * @Date : 2025. 04. 11.
 */
package table.eat.now.reservation.reservation.application.client.dto.response;

import java.time.LocalDateTime;
import java.util.Map;
import lombok.Builder;

@Builder
public record GetCouponsInfo(
    Map<String, Coupon> couponMap
) {

  @Builder
  public record Coupon(
      Long couponId,
      String couponUuid,
      String name,
      String type,
      LocalDateTime startAt,
      LocalDateTime endAt,
      Integer count,
      Boolean allowDuplicate,
      Integer minPurchaseAmount,
      Integer amount,
      Integer percent,
      Integer maxDiscountAmount,
      LocalDateTime createdAt,
      Long createdBy
  ){

  }
}
