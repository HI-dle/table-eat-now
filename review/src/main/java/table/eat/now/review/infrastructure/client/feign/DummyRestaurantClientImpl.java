package table.eat.now.review.infrastructure.client.feign;

import org.springframework.stereotype.Component;
import table.eat.now.review.application.client.RestaurantClient;
import table.eat.now.review.application.service.dto.response.GetRestaurantStaffInfo;

@Component
public class DummyRestaurantClientImpl implements RestaurantClient {

	@Override
	public GetRestaurantStaffInfo getRestaurantStaffInfo(String restaurantId) {
		return new GetRestaurantStaffInfo(1L, 2L);
	}
}
