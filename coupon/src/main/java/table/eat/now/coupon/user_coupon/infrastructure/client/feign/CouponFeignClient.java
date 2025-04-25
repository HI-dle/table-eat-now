package table.eat.now.coupon.user_coupon.infrastructure.client.feign;

import java.util.Set;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import table.eat.now.coupon.user_coupon.infrastructure.client.feign.dto.response.GetCouponsResponseI;
import table.eat.now.coupon.user_coupon.infrastructure.client.feign.config.InternalFeignConfig;

@FeignClient(name="coupon", configuration = InternalFeignConfig.class)
public interface CouponFeignClient {

  @GetMapping("/internal/v1/coupons")
  ResponseEntity<GetCouponsResponseI> getCouponsInternal(@RequestParam Set<String> couponUuids);
}
