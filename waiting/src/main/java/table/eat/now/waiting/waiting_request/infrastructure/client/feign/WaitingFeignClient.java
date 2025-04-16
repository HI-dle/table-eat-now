package table.eat.now.waiting.waiting_request.infrastructure.client.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import table.eat.now.waiting.waiting_request.infrastructure.client.dto.response.GetDailyWaitingResponse;
import table.eat.now.waiting.waiting_request.infrastructure.client.feign.config.InternalFeignConfig;

@FeignClient(name="waiting", configuration = InternalFeignConfig.class)
public interface WaitingFeignClient {

  @GetMapping("/internal/v1/waitings/{dailyWaitingUuid}")
  ResponseEntity<GetDailyWaitingResponse> getDailyWaitingInfo(@PathVariable String dailyWaitingUuid);
}
