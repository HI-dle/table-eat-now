package table.eat.now.reservation.reservation.application.service.dto.response;

import lombok.Builder;
import table.eat.now.reservation.reservation.domain.entity.Reservation.ReservationStatus;

@Builder
public record ConfirmReservationInfo(
    String reservationUuid,
    String paymentIdempotencyKey,
    ReservationStatus status
) {
  public static ConfirmReservationInfo of(
      String reservationUuid,
      String paymentIdempotencyKey,
      ReservationStatus status){
    return ConfirmReservationInfo.builder()
        .reservationUuid(reservationUuid)
        .paymentIdempotencyKey(paymentIdempotencyKey)
        .status(status)
        .build();
  }
}
