/**
 * @author : jieun(je-pa)
 * @Date : 2025. 04. 29.
 */
package table.eat.now.restaurant.restaurant.application.service.dto.request;

import java.math.BigDecimal;
import lombok.Builder;

@Builder
public record RestaurantRatingUpdatedCommand(
    String restaurantUuid,
    BigDecimal averageRating
) {

}
