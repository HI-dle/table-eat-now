/**
 * @author : jieun(je-pa)
 * @Date : 2025. 04. 21.
 */
package table.eat.now.reservation.reservation.application.service.validation.item;

import java.time.Duration;
import java.time.LocalDateTime;
import org.springframework.stereotype.Component;
import table.eat.now.common.exception.CustomException;
import table.eat.now.common.resolver.dto.UserRole;
import table.eat.now.reservation.reservation.application.exception.ReservationErrorCode;
import table.eat.now.reservation.reservation.application.service.validation.context.CancelReservationValidationContext;
import table.eat.now.reservation.reservation.domain.entity.Reservation;

@Component
public class ValidCancelableTime implements ValidItem<CancelReservationValidationContext> {

  private static final long MINIMUM_HOURS_BEFORE_CANCELLATION = 3;

  @Override
  public void validate(CancelReservationValidationContext ctx) {
    validateReservationStatus(ctx.reservation());

    validateCancellationDeadline(
        ctx.cancelRequestDateTime(), 
        ctx.reservation().getRestaurantTimeSlotDetails().reservationDateTime());

    validateEditableUser(ctx.reservation(), ctx.requesterId(), ctx.userRole());
  }

  private static void validateReservationStatus(Reservation reservation) {
    if (reservation.isCanceled()) {
      throw CustomException.from(ReservationErrorCode.ALREADY_CANCELED);
    }
  }

  private static void validateCancellationDeadline(LocalDateTime cancelRequestDateTime,
      LocalDateTime reservationTime) {
    if (Duration.between(cancelRequestDateTime, reservationTime).toHours()
        < MINIMUM_HOURS_BEFORE_CANCELLATION) {
      throw CustomException.from(ReservationErrorCode.CANCELLATION_DEADLINE_PASSED);
    }
  }

  private static void validateEditableUser(Reservation reservation, Long requesterId,
      UserRole requesterUserRole) {
    if (!reservation.isEditableBy(requesterId, requesterUserRole)) {
      throw CustomException.from(ReservationErrorCode.NO_CANCEL_PERMISSION);
    }
  }
}
