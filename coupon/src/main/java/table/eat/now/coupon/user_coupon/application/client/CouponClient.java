package table.eat.now.coupon.user_coupon.application.client;

import java.util.Map;
import java.util.Set;
import table.eat.now.coupon.user_coupon.application.client.dto.response.GetCouponInfoI;

public interface CouponClient {

  Map<String, GetCouponInfoI> getCouponsByCouponUuids(Set<String> couponUuids);

}
