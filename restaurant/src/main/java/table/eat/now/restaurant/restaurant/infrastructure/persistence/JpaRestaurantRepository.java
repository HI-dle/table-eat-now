/**
 * @author : jieun(je-pa)
 * @Date : 2025. 04. 09.
 */
package table.eat.now.restaurant.restaurant.infrastructure.persistence;

import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import table.eat.now.restaurant.restaurant.domain.entity.Restaurant;

public interface JpaRestaurantRepository extends JpaRepository<Restaurant, Long>,
    RestaurantRepositoryCustom {

  @EntityGraph(attributePaths = {"menus", "timeSlots"})
  Optional<Restaurant> findByRestaurantUuid(String restaurantUuid);
}
