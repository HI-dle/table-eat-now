/**
 * @author : jieun(je-pa)
 * @Date : 2025. 04. 10.
 */
package table.eat.now.reservation.reservation.application.client;

import table.eat.now.reservation.reservation.application.client.dto.response.ModifyRestaurantCurTotalGuestCountInfo;
import table.eat.now.reservation.reservation.presentation.dto.response.GetRestaurantInfo;

public interface RestaurantClient {

  GetRestaurantInfo getRestaurant(String restaurantUuid);

  ModifyRestaurantCurTotalGuestCountInfo modifyRestaurantCurTotalGuestCount(
      int delta, String restaurantTimeSlotUuid);
}
