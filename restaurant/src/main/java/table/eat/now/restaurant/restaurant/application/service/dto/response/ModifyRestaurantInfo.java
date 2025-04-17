/**
 * @author : jieun(je-pa)
 * @Date : 2025. 04. 18.
 */
package table.eat.now.restaurant.restaurant.application.service.dto.response;

import lombok.Builder;

@Builder
public record ModifyRestaurantInfo(
    String restaurantUuid,
    String name
) {
}