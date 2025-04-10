/**
 * @author : jieun(je-pa)
 * @Date : 2025. 04. 10.
 */
package table.eat.now.reservation.reservation.presentation;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import table.eat.now.common.aop.annotation.AuthCheck;
import table.eat.now.reservation.reservation.application.service.ReservationService;
import table.eat.now.reservation.reservation.presentation.dto.request.CreateReservationRequest;
import table.eat.now.reservation.reservation.presentation.dto.response.CreateReservationResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/reservations")
public class ReservationController {

  private final ReservationService reservationService;

  @PostMapping
  @AuthCheck
  public ResponseEntity<CreateReservationResponse> createReservation(
      @Valid @RequestBody CreateReservationRequest request) {
    CreateReservationResponse restaurant = CreateReservationResponse
        .from(reservationService.createRestaurant(request.toCommand()));
    return ResponseEntity.created(
        ServletUriComponentsBuilder
            .fromCurrentRequestUri()
            .path("/{restaurantUuid}")
            .buildAndExpand(
                restaurant.restaurantUuid())
            .toUri()
    ).body(restaurant);
  }

}
