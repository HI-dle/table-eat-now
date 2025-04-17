/**
 * @author : jieun(je-pa)
 * @Date : 2025. 04. 17.
 */
package table.eat.now.reservation.reservation.application.service;

import java.time.Duration;
import java.time.LocalDateTime;
import org.springframework.stereotype.Component;
import table.eat.now.common.exception.CustomException;
import table.eat.now.common.resolver.dto.UserRole;
import table.eat.now.reservation.reservation.application.exception.ReservationErrorCode;
import table.eat.now.reservation.reservation.domain.entity.Reservation;

@Component
public class DefaultReservationCancelPolicy implements ReservationCancelPolicy {

  private static final long MINIMUM_HOURS_BEFORE_CANCELLATION = 3;

  // 요부분 예약 생성이랑 같이 후에 좀 더 리펙토링..
  @Override
  public void validCancelableBy(
      Reservation reservation,
      LocalDateTime cancelRequestDateTime,
      Long requesterId,
      UserRole userRole
  ) {
    if (reservation.isCanceled()) {
      throw CustomException.from(ReservationErrorCode.ALREADY_CANCELED);
    }

    LocalDateTime reservationDateTime = reservation.getRestaurantTimeSlotDetails()
        .reservationDateTime();

    // 예약 3시간 전까지만 취소 가능
    Duration durationUntilReservation = Duration.between(cancelRequestDateTime, reservationDateTime);
    if(durationUntilReservation.toHours() < MINIMUM_HOURS_BEFORE_CANCELLATION){
      throw CustomException.from(ReservationErrorCode.CANCELLATION_DEADLINE_PASSED);
    }

    if(!reservation.isEditableBy(requesterId, userRole)){
      throw CustomException.from(ReservationErrorCode.NO_CANCEL_PERMISSION);
    }
  }
}
