/**
 * @author : jieun(je-pa)
 * @Date : 2025. 04. 10.
 */
package table.eat.now.reservation.reservation.infrastructure.client.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import table.eat.now.reservation.reservation.infrastructure.client.feign.dto.request.GetPromotionsRequest;
import table.eat.now.reservation.reservation.infrastructure.client.feign.dto.response.GetPromotionsResponse;

@FeignClient(name = "promotion")
public interface PromotionFeignClient {

  @PostMapping("/internal/v1/promotions")
  ResponseEntity<GetPromotionsResponse> getPromotions(@RequestBody GetPromotionsRequest request);

}
