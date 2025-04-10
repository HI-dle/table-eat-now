/**
 * @author : jieun(je-pa)
 * @Date : 2025. 04. 09.
 */
package table.eat.now.restaurant.restaurant.presentation;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import table.eat.now.common.aop.annotation.AuthCheck;
import table.eat.now.common.resolver.dto.UserRole;
import table.eat.now.restaurant.restaurant.application.service.RestaurantService;
import table.eat.now.restaurant.restaurant.presentation.dto.request.CreateRestaurantRequest;

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
}
