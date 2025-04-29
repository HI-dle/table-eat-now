/**
 * @author : jieun
 * @Date : 2025. 04. 29.
 */
package table.eat.now.restaurant.restaurant.infrastructure.persistence;

import java.util.List;
import table.eat.now.restaurant.restaurant.application.service.dto.request.RestaurantRatingUpdatedCommand;

public interface JdbcRestaurantRepository {

  void batchModifyRestaurantRating(List<RestaurantRatingUpdatedCommand> commands);
}
