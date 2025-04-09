package table.eat.now.review.application.client;

import table.eat.now.review.application.service.dto.response.GetRestaurantStaffInfo;

public interface RestaurantClient {

	GetRestaurantStaffInfo getRestaurantStaffInfo(String restaurantId);

}
