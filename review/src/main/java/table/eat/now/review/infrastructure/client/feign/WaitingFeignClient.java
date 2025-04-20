package table.eat.now.review.infrastructure.client.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import table.eat.now.review.infrastructure.client.config.FeignConfig;
import table.eat.now.review.infrastructure.client.dto.response.GetWaitingRequestResponse;

@FeignClient(name = "waiting", configuration = FeignConfig.class)
public interface WaitingFeignClient {

  @GetMapping("/internal/v1/waiting-requests/{waitingRequestUuid}")
  GetWaitingRequestResponse getWaitingRequest(@PathVariable String waitingRequestUuid);
}
