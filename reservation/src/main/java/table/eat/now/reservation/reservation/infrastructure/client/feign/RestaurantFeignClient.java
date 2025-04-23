/**
 * @author : jieun(je-pa)
 * @Date : 2025. 04. 10.
 */
package table.eat.now.reservation.reservation.infrastructure.client.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import table.eat.now.reservation.reservation.infrastructure.client.feign.dto.response.GetRestaurantResponse;

@FeignClient(name = "restaurant")
public interface RestaurantFeignClient {

  @GetMapping("/internal/v1/restaurants/{restaurantUuid}")
  ResponseEntity<GetRestaurantResponse> getRestaurant(@PathVariable String restaurantUuid);

  @PatchMapping("/internal/v1/restaurants/{restaurantUuid}/timeslot/{restaurantTimeSlotUuid}/cur-total-guest-count")
  ResponseEntity<Void> modifyRestaurantCurTotalGuestCount(
      @RequestBody int delta, @PathVariable String restaurantTimeSlotUuid,
      @PathVariable String restaurantUuid);
}
