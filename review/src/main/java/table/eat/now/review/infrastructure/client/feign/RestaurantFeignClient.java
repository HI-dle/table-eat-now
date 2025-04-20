package table.eat.now.review.infrastructure.client.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import table.eat.now.review.infrastructure.client.config.FeignConfig;
import table.eat.now.review.infrastructure.client.dto.response.GetRestaurantResponse;
import table.eat.now.review.infrastructure.client.dto.response.GetRestaurantSimpleResponse;

@FeignClient(name = "restaurant", configuration = FeignConfig.class)
public interface RestaurantFeignClient {

  @GetMapping("/internal/v1/restaurants/{restaurantUuid}")
  GetRestaurantResponse getRestaurant(@PathVariable String restaurantUuid);

  @GetMapping("/internal/v1/restaurants/my-restaurant")
  GetRestaurantSimpleResponse getRestaurantByStaffId();
}
