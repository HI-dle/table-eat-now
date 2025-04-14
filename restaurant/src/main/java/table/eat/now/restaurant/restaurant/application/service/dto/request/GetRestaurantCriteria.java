/**
 * @author : jieun(je-pa)
 * @Date : 2025. 04. 15.
 */
package table.eat.now.restaurant.restaurant.application.service.dto.request;

import lombok.Builder;
import table.eat.now.common.resolver.dto.UserRole;

@Builder
public record GetRestaurantCriteria (
    String restaurantUuid, UserRole role, Long userId
){

  public static GetRestaurantCriteria from(String restaurantUuid, UserRole role, Long userId) {
    return GetRestaurantCriteria.builder()
        .restaurantUuid(restaurantUuid)
        .role(role)
        .userId(userId)
        .build();
  }
}
