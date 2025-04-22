package table.eat.now.waiting.waiting_request.infrastructure.client;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import table.eat.now.waiting.waiting_request.application.client.RestaurantClient;
import table.eat.now.waiting.waiting_request.application.client.dto.response.GetRestaurantInfo;
import table.eat.now.waiting.waiting_request.infrastructure.client.dto.response.GetRestaurantResponse;
import table.eat.now.waiting.waiting_request.infrastructure.client.feign.RestaurantFeignClient;

@Component
@RequiredArgsConstructor
public class RestaurantClientImpl implements RestaurantClient {
  private final RestaurantFeignClient feignClient;

  @Override
  public GetRestaurantInfo getRestaurantInfo(String restaurantUuid) {
    GetRestaurantResponse response = feignClient.getRestaurant(restaurantUuid).getBody();
    return response.toInfo();
  }
}
