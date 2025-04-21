/**
 * @author : jieun(je-pa)
 * @Date : 2025. 04. 10.
 */
package table.eat.now.reservation.reservation.infrastructure.client;

import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import table.eat.now.reservation.reservation.application.client.CouponClient;
import table.eat.now.reservation.reservation.application.client.dto.request.PreemptCouponCommand;
import table.eat.now.reservation.reservation.application.client.dto.response.GetCouponsInfo;
import table.eat.now.reservation.reservation.infrastructure.client.feign.CouponFeignClient;
import table.eat.now.reservation.reservation.infrastructure.client.feign.dto.request.PreemptCouponRequest;
import table.eat.now.reservation.reservation.infrastructure.client.feign.dto.response.GetCouponsResponse;

@Component
@RequiredArgsConstructor
public class CouponClientImpl implements CouponClient {

  private final CouponFeignClient couponFeignClient;

  @Override
  public GetCouponsInfo getCoupons(Set<String> uuids) {
    GetCouponsResponse coupons = couponFeignClient.getCoupons(uuids).getBody();
    return coupons.toInfo();
  }

  @Override
  public void preemptCoupon(PreemptCouponCommand command) {
    couponFeignClient.preemptCoupon(
        PreemptCouponRequest.from(command.reservationId(), command.userCouponUuids()));
  }
}
