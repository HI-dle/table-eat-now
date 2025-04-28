/**
 * @author : jieun(je-pa)
 * @Date : 2025. 04. 15.
 */
package table.eat.now.restaurant.restaurant.infrastructure.persistence;

import java.util.Optional;
import table.eat.now.restaurant.restaurant.application.service.dto.request.GetRestaurantsCriteria;
import table.eat.now.restaurant.restaurant.domain.dto.response.Paginated;
import table.eat.now.restaurant.restaurant.domain.entity.Restaurant;

public interface RestaurantRepositoryCustom {

  Optional<Restaurant> findByDynamicCondition(String restaurantUuid, boolean includeDeleted,
      boolean includeInactive);

  Paginated<Restaurant> searchRestaurants(GetRestaurantsCriteria criteria);

  boolean isOwnerByUserIdAndRestaurantUuid(Long userId, String restaurantUuid);

  boolean isStaffByUserIdAndRestaurantUuid(Long userId, String restaurantUuid);
}
