/**
 * @author : jieun(je-pa)
 * @Date : 2025. 04. 15.
 */
package table.eat.now.restaurant.restaurant.presentation;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import table.eat.now.common.resolver.annotation.CurrentUserInfo;
import table.eat.now.common.resolver.dto.CurrentUserInfoDto;
import table.eat.now.restaurant.restaurant.application.service.RestaurantService;
import table.eat.now.restaurant.restaurant.application.service.dto.request.GetRestaurantCriteria;
import table.eat.now.restaurant.restaurant.presentation.dto.response.GetRestaurantResponse;
import table.eat.now.restaurant.restaurant.presentation.dto.response.GetRestaurantSimpleResponse;

@RestController
@RequestMapping("/internal/v1/restaurants")
@RequiredArgsConstructor
public class RestaurantInternalController {

  private final RestaurantService restaurantService;

  @GetMapping("/{restaurantUuid}")
  public ResponseEntity<GetRestaurantResponse> getRestaurant(
      @CurrentUserInfo CurrentUserInfoDto userInfo, @PathVariable String restaurantUuid) {
    return ResponseEntity.ok()
        .body(GetRestaurantResponse.from(
            restaurantService.getRestaurant(
                GetRestaurantCriteria.from(restaurantUuid, userInfo.role(), userInfo.userId()))));
  }

  @PatchMapping("/{restaurantUuid}/timeslot/{restaurantTimeSlotUuid}/cur-total-guest-count")
  public ResponseEntity<Void> modifyGuestCount(
      @PathVariable String restaurantUuid,
      @PathVariable String restaurantTimeSlotUuid,
      @RequestParam int delta
  ) {
    restaurantService.increaseOrDecreaseTimeSlotGuestCount(restaurantTimeSlotUuid, delta);
    return ResponseEntity.ok().build();
  }

  @GetMapping
  public ResponseEntity<GetRestaurantSimpleResponse> getRestaurantByStaffId(
      @RequestParam Long staffId) {
    return ResponseEntity.ok()
        .body(GetRestaurantSimpleResponse.from(
            restaurantService.getRestaurantByStaffId(staffId)));
  }
}
