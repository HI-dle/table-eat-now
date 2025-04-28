/**
 * @author : jieun(je-pa)
 * @Date : 2025. 04. 15.
 */
package table.eat.now.restaurant.restaurant.presentation;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import table.eat.now.common.aop.annotation.AuthCheck;
import table.eat.now.common.resolver.annotation.CurrentUserInfo;
import table.eat.now.common.resolver.dto.CurrentUserInfoDto;
import table.eat.now.common.resolver.dto.UserRole;
import table.eat.now.restaurant.restaurant.application.service.RestaurantService;
import table.eat.now.restaurant.restaurant.application.service.dto.request.GetRestaurantCriteria;
import table.eat.now.restaurant.restaurant.presentation.dto.request.SearchRestaurantsRequest;
import table.eat.now.restaurant.restaurant.presentation.dto.response.GetRestaurantResponse;
import table.eat.now.restaurant.restaurant.presentation.dto.response.GetRestaurantSimpleResponse;
import table.eat.now.restaurant.restaurant.presentation.dto.response.PaginatedResponse;
import table.eat.now.restaurant.restaurant.presentation.dto.response.SearchRestaurantsResponse;

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

  @GetMapping
  public ResponseEntity<PaginatedResponse<SearchRestaurantsResponse>> searchRestaurants(
      @Valid SearchRestaurantsRequest request,
      @CurrentUserInfo CurrentUserInfoDto userInfo
  ) {
    return ResponseEntity.ok(PaginatedResponse.from(
        restaurantService.searchRestaurants(request.toCriteria(userInfo))
    ).map(SearchRestaurantsResponse::from));
  }

  @AuthCheck(roles = {UserRole.STAFF, UserRole.OWNER})
  @GetMapping("/my-restaurant")
  public ResponseEntity<GetRestaurantSimpleResponse> getRestaurantByStaffId(
      @CurrentUserInfo CurrentUserInfoDto userInfoDto) {
    return ResponseEntity.ok()
        .body(GetRestaurantSimpleResponse.from(
            restaurantService.getRestaurantByStaffId(userInfoDto.userId())));
  }

  @PatchMapping("/{restaurantUuid}/timeslot/{restaurantTimeSlotUuid}/cur-total-guest-count")
  public ResponseEntity<Void> modifyGuestCount(
      @PathVariable String restaurantUuid,
      @PathVariable String restaurantTimeSlotUuid,
      @RequestBody int delta
  ) {
    restaurantService.increaseOrDecreaseTimeSlotGuestCount(restaurantTimeSlotUuid, delta);
    return ResponseEntity.ok().build();
  }

}
