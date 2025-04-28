/**
 * @author : jieun(je-pa)
 * @Date : 2025. 04. 09.
 */
package table.eat.now.restaurant.restaurant.application.service;

import table.eat.now.restaurant.restaurant.application.service.dto.request.CreateRestaurantCommand;
import table.eat.now.restaurant.restaurant.application.service.dto.request.GetRestaurantCriteria;
import table.eat.now.restaurant.restaurant.application.service.dto.request.GetRestaurantsCriteria;
import table.eat.now.restaurant.restaurant.application.service.dto.request.ModifyRestaurantCommand;
import table.eat.now.restaurant.restaurant.application.service.dto.response.CreateRestaurantInfo;
import table.eat.now.restaurant.restaurant.application.service.dto.response.GetRestaurantInfo;
import table.eat.now.restaurant.restaurant.application.service.dto.response.GetRestaurantSimpleInfo;
import table.eat.now.restaurant.restaurant.application.service.dto.response.ModifyRestaurantInfo;
import table.eat.now.restaurant.restaurant.application.service.dto.response.PaginatedInfo;
import table.eat.now.restaurant.restaurant.application.service.dto.response.SearchRestaurantsInfo;

public interface RestaurantService {

  CreateRestaurantInfo createRestaurant(CreateRestaurantCommand command);

  GetRestaurantInfo getRestaurant(GetRestaurantCriteria criteria);

  GetRestaurantSimpleInfo getRestaurantByStaffId(Long staffId);

  PaginatedInfo<SearchRestaurantsInfo> searchRestaurants(GetRestaurantsCriteria criteria);

  void increaseOrDecreaseTimeSlotGuestCount(String restaurantTimeSlotUuid, int delta);

  ModifyRestaurantInfo modifyRestaurant(ModifyRestaurantCommand command);
}
