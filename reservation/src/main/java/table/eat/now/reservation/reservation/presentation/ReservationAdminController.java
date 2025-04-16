/**
 * @author : jieun(je-pa)
 * @Date : 2025. 04. 16.
 */
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
import table.eat.now.common.resolver.dto.UserRole;
import table.eat.now.reservation.reservation.application.service.ReservationService;
import table.eat.now.reservation.reservation.application.service.dto.request.GetReservationCriteria;
import table.eat.now.reservation.reservation.presentation.dto.response.GetReservationResponse;

@RestController
@RequestMapping("/admin/v1/reservations")
@RequiredArgsConstructor
public class ReservationAdminController {
  private final ReservationService reservationService;

  @GetMapping("/{reservationUuid}")
  @AuthCheck(roles = {UserRole.MASTER, UserRole.OWNER, UserRole.STAFF})
  public ResponseEntity<GetReservationResponse> getReservationAdmin(
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
