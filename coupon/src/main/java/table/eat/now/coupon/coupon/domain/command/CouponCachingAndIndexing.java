package table.eat.now.coupon.coupon.domain.command;

import com.fasterxml.jackson.core.type.TypeReference;
import java.util.Map;
import lombok.Builder;
import table.eat.now.coupon.coupon.application.utils.MapperProvider;
import table.eat.now.coupon.coupon.application.utils.TimeProvider;
import table.eat.now.coupon.coupon.domain.entity.Coupon;
import table.eat.now.coupon.coupon.domain.entity.CouponLabel;

@Builder
public record CouponCachingAndIndexing(
    String couponUuid,
    CouponLabel label,
    Map<String, Object> couponMap,
    Long expiredAt
) {

  public static CouponCachingAndIndexing from(Coupon coupon) {
    return CouponCachingAndIndexing.builder()
        .couponUuid(coupon.getCouponUuid())
        .label(coupon.getLabel())
        .couponMap(MapperProvider.convertValue(coupon, new TypeReference<>() {}))
        .expiredAt(TimeProvider.getEpochMillis(coupon.calcExpireAt()))
        .build();
  }
}
