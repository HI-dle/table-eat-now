/**
 * @author : jieun(je-pa)
 * @Date : 2025. 04. 09.
 */
package table.eat.now.restaurant.restaurant.presentation;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import table.eat.now.common.aop.annotation.AuthCheck;
import table.eat.now.common.resolver.annotation.CurrentUserInfo;
import table.eat.now.common.resolver.dto.CurrentUserInfoDto;
import table.eat.now.common.resolver.dto.UserRole;
import table.eat.now.restaurant.restaurant.application.service.RestaurantService;
import table.eat.now.restaurant.restaurant.application.service.dto.request.GetRestaurantCriteria;
import table.eat.now.restaurant.restaurant.application.service.dto.request.ModifyRestaurantCommand;
import table.eat.now.restaurant.restaurant.application.service.dto.response.ModifyRestaurantInfo;
import table.eat.now.restaurant.restaurant.presentation.dto.request.CreateRestaurantRequest;
import table.eat.now.restaurant.restaurant.presentation.dto.request.ModifyRestaurantRequest;
import table.eat.now.restaurant.restaurant.presentation.dto.response.GetRestaurantResponse;
import table.eat.now.restaurant.restaurant.presentation.dto.response.ModifyRestaurantResponse;

@RestController
@RequestMapping("/admin/v1/restaurants")
@RequiredArgsConstructor
public class RestaurantAdminController {

  private final RestaurantService restaurantService;


  @PostMapping
  @AuthCheck(roles = {UserRole.MASTER, UserRole.OWNER})
  public ResponseEntity<Void> createRestaurant(
      @Valid @RequestBody CreateRestaurantRequest request) {
    return ResponseEntity.created(
        ServletUriComponentsBuilder
            .fromCurrentRequestUri()
            .path("/{restaurantUuid}")
            .buildAndExpand(
                restaurantService.createRestaurant(request.toCommand()).restaurantUuid())
            .toUri()
        ).build();
  }

  @GetMapping("/{restaurantUuid}")
  @AuthCheck(roles = {UserRole.MASTER, UserRole.OWNER, UserRole.STAFF})
  public ResponseEntity<GetRestaurantResponse> getRestaurant(
      @CurrentUserInfo CurrentUserInfoDto userInfo, @PathVariable String restaurantUuid) {
    return ResponseEntity.ok()
        .body(GetRestaurantResponse.from(
            restaurantService.getRestaurant(
                GetRestaurantCriteria.from(restaurantUuid, userInfo.role(), userInfo.userId()))));
  }

  @PutMapping("/{restaurantUuid}")
  @AuthCheck(roles = {UserRole.MASTER, UserRole.OWNER})
  public ResponseEntity<ModifyRestaurantResponse> modifyRestaurant(
      @CurrentUserInfo CurrentUserInfoDto userInfo,
      @PathVariable String restaurantUuid,
      @Valid @RequestBody ModifyRestaurantRequest request) {
    ModifyRestaurantCommand command = request.toCommand(restaurantUuid, userInfo.userId(), userInfo.role());
    ModifyRestaurantInfo info = restaurantService.modifyRestaurant(command);
    return ResponseEntity.ok(ModifyRestaurantResponse.from(info));
  }
}
