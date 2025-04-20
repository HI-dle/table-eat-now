package table.eat.now.review.application.client;

import table.eat.now.review.application.client.dto.GetRestaurantInfo;
import table.eat.now.review.application.client.dto.GetRestaurantStaffInfo;

public interface RestaurantClient {

  GetRestaurantStaffInfo getRestaurantStaffs(String restaurantId);

  GetRestaurantInfo getRestaurant(Long userId);

}
