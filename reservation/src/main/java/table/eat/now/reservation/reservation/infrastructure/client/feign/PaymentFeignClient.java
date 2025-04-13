/**
 * @author : jieun(je-pa)
 * @Date : 2025. 04. 10.
 */
package table.eat.now.reservation.reservation.infrastructure.client.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import table.eat.now.reservation.reservation.infrastructure.client.feign.dto.request.CreatePaymentRequest;
import table.eat.now.reservation.reservation.infrastructure.client.feign.dto.response.CreatePaymentResponse;

@FeignClient(name = "payment")
public interface PaymentFeignClient {

  @PostMapping("/internal/v1/payments")
  ResponseEntity<CreatePaymentResponse> createPayment(@RequestBody CreatePaymentRequest request);
}
