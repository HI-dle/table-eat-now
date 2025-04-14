package table.eat.now.waiting.waiting_request.infrastructure.client.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import table.eat.now.waiting.waiting_request.infrastructure.client.dto.response.GetRestaurantResponse;
import table.eat.now.waiting.waiting_request.infrastructure.client.feign.config.InternalFeignConfig;

@FeignClient(name="restaurant", configuration = InternalFeignConfig.class)
public interface RestaurantFeignClient {

  @GetMapping("/internal/v1/restaurants/{restaurantUuid}")
  ResponseEntity<GetRestaurantResponse> getRestaurant(@PathVariable String restaurantUuid);
}
