/**
 * @author : jieun(je-pa)
 * @Date : 2025. 04. 15.
 */
package table.eat.now.restaurant.restaurant.presentation;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import table.eat.now.common.resolver.annotation.CurrentUserInfo;
import table.eat.now.common.resolver.dto.CurrentUserInfoDto;
import table.eat.now.restaurant.restaurant.application.service.RestaurantService;
import table.eat.now.restaurant.restaurant.application.service.dto.request.GetRestaurantCriteria;
import table.eat.now.restaurant.restaurant.presentation.dto.response.GetRestaurantResponse;

@RestController
@RequestMapping("/api/v1/restaurants")
@RequiredArgsConstructor
public class RestaurantApiController {
  private final RestaurantService restaurantService;

  @GetMapping("/{restaurantUuid}")
  public ResponseEntity<GetRestaurantResponse> getRestaurant(
      @CurrentUserInfo CurrentUserInfoDto userInfo, @PathVariable String restaurantUuid) {
    return ResponseEntity.ok()
        .body(GetRestaurantResponse.from(
            restaurantService.getRestaurant(
                GetRestaurantCriteria.from(restaurantUuid, userInfo.role(), userInfo.userId()))));
  }
}
