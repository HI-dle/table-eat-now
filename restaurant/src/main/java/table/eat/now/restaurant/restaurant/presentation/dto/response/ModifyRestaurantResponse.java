/**
 * @author : jieun(je-pa)
 * @Date : 2025. 04. 18.
 */
package table.eat.now.restaurant.restaurant.presentation.dto.response;


import lombok.Builder;
import table.eat.now.restaurant.restaurant.application.service.dto.response.ModifyRestaurantInfo;

public record ModifyRestaurantResponse(
    String restaurantUuid,
    String name
) {
  @Builder
  public ModifyRestaurantResponse {}

  public static ModifyRestaurantResponse from(ModifyRestaurantInfo info) {
    return ModifyRestaurantResponse.builder()
        .restaurantUuid(info.restaurantUuid())
        .name(info.name())
        .build();
  }
}
