package table.eat.now.review.infrastructure.client.feign;

import java.util.UUID;
import org.springframework.stereotype.Component;
import table.eat.now.review.application.client.RestaurantClient;
import table.eat.now.review.application.service.dto.response.GetRestaurantInfo;
import table.eat.now.review.application.service.dto.response.GetRestaurantStaffInfo;

@Component
public class DummyRestaurantClientImpl implements RestaurantClient {

  @Override
  public GetRestaurantStaffInfo getRestaurantStaffInfo(String restaurantId) {
    return new GetRestaurantStaffInfo(1L, 2L);
  }

  @Override
  public GetRestaurantInfo getRestaurantInfo(Long userId) {
    return new GetRestaurantInfo(UUID.randomUUID().toString());
  }
}
