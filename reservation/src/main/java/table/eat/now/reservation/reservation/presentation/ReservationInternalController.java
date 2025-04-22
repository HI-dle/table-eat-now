package table.eat.now.reservation.reservation.presentation;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import table.eat.now.common.aop.annotation.AuthCheck;
import table.eat.now.common.resolver.annotation.CurrentUserInfo;
import table.eat.now.common.resolver.dto.CurrentUserInfoDto;
import table.eat.now.reservation.reservation.application.service.ReservationService;
import table.eat.now.reservation.reservation.application.service.dto.request.GetReservationCriteria;
import table.eat.now.reservation.reservation.presentation.dto.response.GetReservationResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/internal/v1/reservations")
public class ReservationInternalController {

  private final ReservationService reservationService;

  @GetMapping("/{reservationUuid}")
  @AuthCheck
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
