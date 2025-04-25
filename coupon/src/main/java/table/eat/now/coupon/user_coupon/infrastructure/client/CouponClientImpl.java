package table.eat.now.coupon.user_coupon.infrastructure.client;

import java.util.Map;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import table.eat.now.coupon.user_coupon.application.client.CouponClient;
import table.eat.now.coupon.user_coupon.application.client.dto.response.GetCouponInfoI;
import table.eat.now.coupon.user_coupon.infrastructure.client.feign.CouponFeignClient;

@RequiredArgsConstructor
@Component
public class CouponClientImpl implements CouponClient {
  private final CouponFeignClient couponFeignClient;

  @Override
  public Map<String, GetCouponInfoI> getCouponsByCouponUuids(Set<String> couponUuids) {
    return couponFeignClient.getCouponsInternal(couponUuids).getBody().toInfo();
  }
}
