package table.eat.now.reservation.reservation.application.service.validation.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import table.eat.now.common.exception.CustomException;
import table.eat.now.reservation.reservation.application.exception.ReservationErrorCode;
import table.eat.now.reservation.reservation.application.service.validation.context.ConfirmReservationValidationContext;
import table.eat.now.reservation.reservation.domain.entity.Reservation;

@Component
@RequiredArgsConstructor
public class ValidReservationStatusForConfirm implements ValidItem<ConfirmReservationValidationContext> {

  @Override
  public void validate(ConfirmReservationValidationContext context) {
    Reservation reservation = context.reservation();
    if(reservation.isValidStatusForConfirmation()) return;
    throw CustomException.from(ReservationErrorCode.INVALID_STATUS_FOR_CONFIRMATION);
  }
}
