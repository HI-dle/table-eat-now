/**
 * @author : jieun(je-pa)
 * @Date : 2025. 04. 09.
 */
package table.eat.now.restaurant.restaurant.domain.repository;

import java.util.List;
import table.eat.now.restaurant.restaurant.domain.entity.Restaurant;

public interface RestaurantRepository {

  Restaurant save(Restaurant entity);

  // test 용
  List<Restaurant> findAll();
}
