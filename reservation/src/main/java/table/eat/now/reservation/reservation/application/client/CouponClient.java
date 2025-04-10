/**
 * @author : jieun(je-pa)
 * @Date : 2025. 04. 10.
 */
package table.eat.now.reservation.reservation.application.client;

import java.util.Set;
import table.eat.now.reservation.reservation.application.client.dto.response.GetCouponsInfo;

public interface CouponClient {

  GetCouponsInfo getCoupons(Set<String> uuids);

}
