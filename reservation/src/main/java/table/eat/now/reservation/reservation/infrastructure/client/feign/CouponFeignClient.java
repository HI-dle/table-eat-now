/**
 * @author : jieun(je-pa)
 * @Date : 2025. 04. 10.
 */
package table.eat.now.reservation.reservation.infrastructure.client.feign;

import java.util.Set;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import table.eat.now.reservation.reservation.infrastructure.client.feign.dto.request.PreemptCouponRequest;
import table.eat.now.reservation.reservation.infrastructure.client.feign.dto.response.GetCouponsResponse;

@FeignClient(name = "coupon")
public interface CouponFeignClient {

  @GetMapping("/internal/v1/coupons")
  ResponseEntity<GetCouponsResponse> getCoupons(@RequestParam Set<String> ids);

  @PatchMapping("/internal/v1/user-coupons/preempt")
  ResponseEntity<Void> preemptCoupon(@RequestBody PreemptCouponRequest request);
}
