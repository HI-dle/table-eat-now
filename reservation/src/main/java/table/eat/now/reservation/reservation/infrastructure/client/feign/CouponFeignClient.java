/**
 * @author : jieun(je-pa)
 * @Date : 2025. 04. 10.
 */
package table.eat.now.reservation.reservation.infrastructure.client.feign;

import java.util.Set;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import table.eat.now.reservation.reservation.infrastructure.client.feign.dto.response.GetCouponsResponse;

@FeignClient(name = "coupon-service")
public interface CouponFeignClient {

  @GetMapping("/internal/v1/coupons")
  ResponseEntity<GetCouponsResponse> getCoupons(@RequestParam Set<String> ids);
}
