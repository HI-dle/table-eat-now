/**
 * @author : jieun(je-pa)
 * @Date : 2025. 04. 17.
 */
package table.eat.now.reservation.reservation.presentation.dto.response;

import lombok.Builder;
import table.eat.now.reservation.reservation.application.service.dto.response.CancelReservationInfo;

@Builder
public record CancelReservationResponse(
    String reservationUuid,
    String status
) {

  public static CancelReservationResponse from(CancelReservationInfo response) {
    return CancelReservationResponse.builder()
        .reservationUuid(response.reservationUuid())
        .status(response.status())
        .build();
  }
}
