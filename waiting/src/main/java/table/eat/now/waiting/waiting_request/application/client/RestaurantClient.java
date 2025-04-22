package table.eat.now.waiting.waiting_request.application.client;

import table.eat.now.waiting.waiting_request.application.client.dto.response.GetRestaurantInfo;

public interface RestaurantClient {

  GetRestaurantInfo getRestaurantInfo(String restaurantUuid);
}
