/**
 * @author : jieun(je-pa)
 * @Date : 2025. 04. 10.
 */
package table.eat.now.reservation.reservation.application.client;

import java.util.Set;
import table.eat.now.reservation.reservation.application.client.dto.request.PreemptCouponCommand;
import table.eat.now.reservation.reservation.application.client.dto.response.GetUserCouponsInfo;

public interface CouponClient {

  GetUserCouponsInfo getUserCoupons(Set<String> uuids);

  void preemptCoupon(PreemptCouponCommand command);
}
