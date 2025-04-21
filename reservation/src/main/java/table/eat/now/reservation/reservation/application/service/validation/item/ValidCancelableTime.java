/**
 * @author : jieun(je-pa)
 * @Date : 2025. 04. 21.
 */
package table.eat.now.reservation.reservation.application.service.validation.item;

import java.time.Duration;
import java.time.LocalDateTime;
import org.springframework.stereotype.Component;
import table.eat.now.common.exception.CustomException;
import table.eat.now.reservation.reservation.application.exception.ReservationErrorCode;
import table.eat.now.reservation.reservation.application.service.validation.context.CancelReservationValidationContext;

@Component
public class ValidCancelableTime implements ValidItem<CancelReservationValidationContext> {

  private static final long MINIMUM_HOURS_BEFORE_CANCELLATION = 3;

  @Override
  public void validate(CancelReservationValidationContext ctx) {
    if (ctx.reservation().isCanceled()) {
      throw CustomException.from(ReservationErrorCode.ALREADY_CANCELED);
    }

    LocalDateTime reservationTime = ctx.reservation()
        .getRestaurantTimeSlotDetails().reservationDateTime();

    if (Duration.between(ctx.cancelRequestDateTime(), reservationTime).toHours()
        < MINIMUM_HOURS_BEFORE_CANCELLATION) {
      throw CustomException.from(ReservationErrorCode.CANCELLATION_DEADLINE_PASSED);
    }

    if (!ctx.reservation().isEditableBy(ctx.requesterId(), ctx.userRole())) {
      throw CustomException.from(ReservationErrorCode.NO_CANCEL_PERMISSION);
    }
  }
}
