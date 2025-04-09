/**
 * @author : jieun(je-pa)
 * @Date : 2025. 04. 09.
 */
package table.eat.now.restaurant.restaurant.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import table.eat.now.restaurant.restaurant.domain.entity.Restaurant;
import table.eat.now.restaurant.restaurant.domain.repository.RestaurantRepository;

public interface JpaRestaurantRepository extends JpaRepository<Restaurant, Long>,
    RestaurantRepository {

}
