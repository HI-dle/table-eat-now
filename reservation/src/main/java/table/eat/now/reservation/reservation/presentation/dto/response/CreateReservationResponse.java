/**
 * @author : jieun(je-pa)
 * @Date : 2025. 04. 11.
 */
package table.eat.now.reservation.reservation.presentation.dto.response;

import table.eat.now.reservation.reservation.application.service.dto.response.CreateReservationInfo;

public record CreateReservationResponse (
    String restaurantUuid,
    String paymentReferenceKey
){

  public static CreateReservationResponse from(CreateReservationInfo restaurant) {
    return new CreateReservationResponse(restaurant.restaurantUuid(), restaurant.paymentReferenceKey());
  }
}
