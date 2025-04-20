package table.eat.now.review.infrastructure.client;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import table.eat.now.review.application.client.RestaurantClient;
import table.eat.now.review.application.client.dto.GetRestaurantInfo;
import table.eat.now.review.application.client.dto.GetRestaurantStaffInfo;
import table.eat.now.review.infrastructure.client.feign.RestaurantFeignClient;

@Component
@RequiredArgsConstructor
public class RestaurantClientImpl implements RestaurantClient {

  private final RestaurantFeignClient restaurantFeignClient;

  @Override
  public GetRestaurantStaffInfo getRestaurantStaffs(String restaurantId) {
    return restaurantFeignClient.getRestaurant(restaurantId).toInfo();
  }

  @Override
  public GetRestaurantInfo getRestaurant(Long staffId) {
    return restaurantFeignClient.getRestaurantByStaffId().toInfo();
  }
}
