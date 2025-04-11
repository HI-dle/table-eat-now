/**
 * @author : jieun(je-pa)
 * @Date : 2025. 04. 11.
 */
package table.eat.now.reservation.reservation.presentation.dto.response;

import table.eat.now.reservation.reservation.application.service.dto.response.CreateReservationInfo;

public record CreateReservationResponse (
    String reservationUuid,
    String paymentReferenceKey
){

  public static CreateReservationResponse from(CreateReservationInfo reservation) {
    return new CreateReservationResponse(reservation.reservationUuid(), reservation.paymentReferenceKey());
  }
}
