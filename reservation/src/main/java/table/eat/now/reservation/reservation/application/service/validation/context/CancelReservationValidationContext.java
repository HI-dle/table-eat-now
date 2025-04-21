/**
 * @author : jieun(je-pa)
 * @Date : 2025. 04. 21.
 */
package table.eat.now.reservation.reservation.application.service.validation.context;

import java.time.LocalDateTime;
import lombok.Builder;
import table.eat.now.common.resolver.dto.UserRole;
import table.eat.now.reservation.reservation.domain.entity.Reservation;

@Builder
public record CancelReservationValidationContext(
    Reservation reservation,
    LocalDateTime cancelRequestDateTime,
    Long requesterId,
    UserRole userRole
) {

  public static CancelReservationValidationContext from(
      Reservation reservation,
      LocalDateTime cancelRequestDateTime,
      Long requesterId,
      UserRole requesterUserRole) {
    return CancelReservationValidationContext.builder()
        .reservation(reservation)
        .cancelRequestDateTime(cancelRequestDateTime)
        .requesterId(requesterId)
        .userRole(requesterUserRole)
        .build();
  }
}
