/**
 * @author : jieun(je-pa)
 * @Date : 2025. 04. 10.
 */
package table.eat.now.reservation.reservation.presentation;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import table.eat.now.common.aop.annotation.AuthCheck;
import table.eat.now.common.resolver.annotation.CurrentUserInfo;
import table.eat.now.common.resolver.dto.CurrentUserInfoDto;
import table.eat.now.reservation.reservation.application.service.ReservationService;
import table.eat.now.reservation.reservation.application.service.dto.request.GetReservationCriteria;
import table.eat.now.reservation.reservation.presentation.dto.request.CreateReservationRequest;
import table.eat.now.reservation.reservation.presentation.dto.response.CreateReservationResponse;
import table.eat.now.reservation.reservation.presentation.dto.response.GetReservationResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/reservations")
public class ReservationApiController {

  private final ReservationService reservationService;

  @PostMapping
  @AuthCheck
  public ResponseEntity<CreateReservationResponse> createReservation(
      @CurrentUserInfo CurrentUserInfoDto userInfo,
      @Valid @RequestBody CreateReservationRequest request) {
    CreateReservationResponse reservation = CreateReservationResponse
        .from(reservationService.createReservation(
            request.toCommand(userInfo.userId(), LocalDateTime.now())));
    return ResponseEntity.created(
        ServletUriComponentsBuilder
            .fromCurrentRequestUri()
            .path("/{reservationUuid}")
            .buildAndExpand(
                reservation.reservationUuid())
            .toUri()
    ).body(reservation);
  }

  @GetMapping("/{reservationUuid}")
  public ResponseEntity<GetReservationResponse> getReservation(
      @CurrentUserInfo CurrentUserInfoDto userInfo,
      @PathVariable String reservationUuid
  ) {
    return ResponseEntity.ok(
        GetReservationResponse.from(
            reservationService.getReservation(
                GetReservationCriteria.from(reservationUuid, userInfo.role(), userInfo.userId())
            )
        )
    );
  }

}
