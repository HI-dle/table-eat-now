/**
 * @author : jieun(je-pa)
 * @Date : 2025. 04. 09.
 */
package table.eat.now.restaurant.restaurant.application.service;

import table.eat.now.restaurant.restaurant.application.service.dto.request.CreateRestaurantCommand;
import table.eat.now.restaurant.restaurant.application.service.dto.response.CreateRestaurantInfo;

public interface RestaurantService {

  CreateRestaurantInfo createRestaurant(CreateRestaurantCommand command);
}
