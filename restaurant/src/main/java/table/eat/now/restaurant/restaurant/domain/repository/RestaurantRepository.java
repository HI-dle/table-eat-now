/**
 * @author : jieun(je-pa)
 * @Date : 2025. 04. 09.
 */
package table.eat.now.restaurant.restaurant.domain.repository;

import java.util.List;
import java.util.Optional;
import table.eat.now.restaurant.restaurant.application.service.dto.request.GetRestaurantsCriteria;
import table.eat.now.restaurant.restaurant.domain.dto.response.Paginated;
import table.eat.now.restaurant.restaurant.domain.entity.Restaurant;

public interface RestaurantRepository {

  Restaurant save(Restaurant entity);

  Optional<Restaurant> findByRestaurantUuidWithMenusAndTimeslots(String restaurantUuid);

  Optional<Restaurant> findByDynamicCondition(String restaurantUuid, boolean includeDeleted,
      boolean includeInactive);

  Optional<Restaurant> findByStaffIdOrOwnerId(Long id);

  Paginated<Restaurant> searchRestaurants(GetRestaurantsCriteria criteria);

  boolean isOwner(Long userId, String restaurantUuid);

  boolean isStaff(Long userId, String restaurantUuid);

  // test 용
  List<Restaurant> findAll();

  <S extends Restaurant> List<S> saveAll(Iterable<S> restaurants);

}
