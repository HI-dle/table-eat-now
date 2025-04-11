/**
 * @author : jieun(je-pa)
 * @Date : 2025. 04. 10.
 */
package table.eat.now.reservation.reservation.application.service.dto.response;

public record CreateReservationInfo(
    String reservationUuid,
    String paymentReferenceKey
) {

  public static CreateReservationInfo of(String reservationUuid, String paymentKey) {
    return new CreateReservationInfo(reservationUuid, paymentKey);
  }
}
