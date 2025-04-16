/**
 * @author : jieun(je-pa)
 * @Date : 2025. 04. 17.
 */
package table.eat.now.reservation.reservation.application.service.dto.response;

import lombok.Builder;
import table.eat.now.reservation.reservation.domain.entity.Reservation;

@Builder
public record CancelReservationInfo(
    String reservationUuid,
    String status
) {

  public static CancelReservationInfo from(Reservation reservation) {
    return CancelReservationInfo.builder()
        .reservationUuid(reservation.getReservationUuid())
        .status(reservation.getStatus().toString())
        .build();
  }
}
