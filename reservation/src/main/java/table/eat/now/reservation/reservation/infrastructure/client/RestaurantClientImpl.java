/**
 * @author : jieun(je-pa)
 * @Date : 2025. 04. 10.
 */
package table.eat.now.reservation.reservation.infrastructure.client;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import table.eat.now.reservation.reservation.application.client.RestaurantClient;
import table.eat.now.reservation.reservation.application.client.dto.response.ModifyRestaurantCurTotalGuestCountInfo;
import table.eat.now.reservation.reservation.infrastructure.client.feign.RestaurantFeignClient;
import table.eat.now.reservation.reservation.infrastructure.client.feign.dto.response.GetRestaurantResponse;
import table.eat.now.reservation.reservation.infrastructure.client.feign.dto.response.ModifyRestaurantCurTotalGuestCountResponse;
import table.eat.now.reservation.reservation.presentation.dto.response.GetRestaurantInfo;

@Component
@RequiredArgsConstructor
public class RestaurantClientImpl implements RestaurantClient {
  private final RestaurantFeignClient restaurantFeignClient;

  @Override
  public GetRestaurantInfo getRestaurant(String restaurantUuid) {
    GetRestaurantResponse restaurant = restaurantFeignClient.getRestaurant(restaurantUuid).getBody();
    return restaurant.toInfo();
  }

  @Override
  public ModifyRestaurantCurTotalGuestCountInfo modifyRestaurantCurTotalGuestCount(
      int delta, String restaurantTimeSlotUuid, String restaurantUuid) {
    ModifyRestaurantCurTotalGuestCountResponse response =
        restaurantFeignClient
            .modifyRestaurantCurTotalGuestCount(delta, restaurantTimeSlotUuid, restaurantUuid)
            .getBody();
    return response.toInfo();
  }
}
