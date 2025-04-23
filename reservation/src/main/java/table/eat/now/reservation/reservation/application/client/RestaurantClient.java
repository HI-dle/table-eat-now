/**
 * @author : jieun(je-pa)
 * @Date : 2025. 04. 10.
 */
package table.eat.now.reservation.reservation.application.client;

import table.eat.now.reservation.reservation.application.service.dto.response.GetRestaurantInfo;

public interface RestaurantClient {

  GetRestaurantInfo getRestaurant(String restaurantUuid);

  void modifyRestaurantCurTotalGuestCount(
      int delta, String restaurantTimeSlotUuid, String restaurantUuid);
}
