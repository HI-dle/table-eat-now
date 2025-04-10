/**
 * @author : jieun(je-pa)
 * @Date : 2025. 04. 10.
 */
package table.eat.now.reservation.reservation.infrastructure.client.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import table.eat.now.reservation.reservation.infrastructure.client.feign.dto.response.GetRestaurantResponse;
import table.eat.now.reservation.reservation.infrastructure.client.feign.dto.response.ModifyRestaurantCurTotalGuestCountResponse;

@FeignClient(name = "restaurant")
public interface RestaurantFeignClient {

  @PatchMapping("/internal/v1/restaurants/{restaurantUuid}")
  ResponseEntity<GetRestaurantResponse> getRestaurant(@PathVariable String restaurantUuid);

  ResponseEntity<ModifyRestaurantCurTotalGuestCountResponse> modifyRestaurantCurTotalGuestCount(int delta);
}
