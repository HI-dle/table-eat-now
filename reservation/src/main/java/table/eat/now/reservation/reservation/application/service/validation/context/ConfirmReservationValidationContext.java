package table.eat.now.reservation.reservation.application.service.validation.context;

import lombok.Builder;
import table.eat.now.reservation.reservation.domain.entity.Reservation;

@Builder
public record ConfirmReservationValidationContext(
    Reservation reservation
) {
  public static ConfirmReservationValidationContext from(Reservation reservation) {
    return new ConfirmReservationValidationContext(reservation);
  }
}
